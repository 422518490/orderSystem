package com.yaya.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.yaya.common.constant.MerchantEnableConstant;
import com.yaya.common.constant.UserTypeConstant;
import com.yaya.common.response.*;
import com.yaya.common.util.*;
import com.yaya.merchant.orderApi.PermissionInterface;
import com.yaya.merchant.service.MerchantService;
import com.yaya.merchant.setting.MerchantRedisSetting;
import com.yaya.merchant.template.MerchantExportResult;
import com.yaya.orderApi.CurrentUserData;
import com.yaya.orderApi.merchantDTO.MerchantDTO;
import com.yaya.orderApi.merchantInterface.MerchantControllerInterface;
import com.yaya.orderApi.uploadFileDTO.UploadFileDTO;
import com.yaya.orderApi.uploadFileInterface.UploadFileControllerInterface;
import io.rebloom.client.Client;
import io.rebloom.client.ClusterClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 商家controller类
 */
@RestController
@Slf4j
public class MerchantController implements UploadFileControllerInterface, MerchantControllerInterface {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private BaiDuMapUtil baiDuMapUtil;

    @Autowired
    private SetSelfRedis setSelfRedis;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PermissionInterface permissionInterface;

    @Autowired
    private UploadFileUtil uploadFileUtil;

    @Resource
    private Client bloomClient;

    @Resource
    private ClusterClient clusterClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MerchantRedisSetting merchantRedisSetting;

    /**
     * 商家登陆
     *
     * @param merchantDTO
     * @param response
     * @return
     */
    @RequestMapping(value = "/merchant/merchantLogin", method = RequestMethod.POST)
    //@AccessRequired
    public CommonResponse<MerchantDTO> merchantLogin(@RequestBody MerchantDTO merchantDTO,
                                                     HttpServletResponse response) {
        CommonResponse<MerchantDTO> commonResponse = new CommonResponse<>();
        try {
            MerchantDTO merchant = merchantService.loginByMerchantName(merchantDTO);
            Map<String, String> errorMap = validateLogin(merchant, merchantDTO.getMerchantPassword());
            // 判断是否有错误信息
            if (errorMap.size() > 0) {
                commonResponse.setCode(ResponseCode.PARAMETER_ERROR);
                commonResponse.setErrorMap(errorMap);
                return commonResponse;
            }

            merchantDTO = parseCacheMerchantDTO(merchant);

            String accessToken = AccessTokenUtil.createAccessToken(merchantDTO.getMerchantLoginName(), String.valueOf(System.currentTimeMillis()));
            response.setHeader("accessToken", accessToken);
            //redisUtil.set("testsentinel","456");
            redisUtil.set("testsentinel", "456", 1L);
            System.out.println(redisUtil.get("testsentinel"));
            List<String> methodNameList = permissionInterface.getMethodNameByType(merchantDTO.getUserType()).getData();
            Long invalid = Long.parseLong(setSelfRedis.getSessionInvalid()) * 60;

            // 缓存用户信息
            CurrentUserData currentUserData = new CurrentUserData();
            currentUserData.setUserId(merchantDTO.getMerchantId());
            currentUserData.setUserName(merchantDTO.getMerchantName());
            currentUserData.setUserType(merchantDTO.getUserType());
            currentUserData.setPhone(merchantDTO.getMerchantTelPhone());
            currentUserData.setMethodNameList(methodNameList);
            redisUtil.set(accessToken, currentUserData, invalid);

            // 清除商家敏感信息
            merchantDTO.setMerchantPassword("");
            merchantDTO.setSalt("");

            commonResponse.setData(merchantDTO);
            commonResponse.setCode(ResponseCode.SUCCESS);
            commonResponse.setMsg("登陆成功");
        } catch (Exception e) {
            commonResponse.setCode(ResponseCode.SERVER_ERROR);
            commonResponse.setMsg("服务器错误");
            log.error("商家登陆错误:{}", e);
        }
        return commonResponse;
    }

    /**
     * 商家注册
     *
     * @param merchantDTO
     * @return
     */
    @RequestMapping(value = "/merchant/merchantRegister", method = RequestMethod.POST)
    public BaseResponse merchantRegister(@RequestBody MerchantDTO merchantDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            // 验证注册信息
            Map<String, String> errMap = validateRegister(merchantDTO);
            if (errMap.size() > 0) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setErrorMap(errMap);
                return baseResponse;
            }

            merchantDTO = merchantService.merchantRegister(merchantDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("注册成功");
        } catch (Exception e) {
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            log.error("商家注册错误:{}", e);
        }
        return baseResponse;
    }

    /**
     * 对外提供获取特定商家信息
     *
     * @param merchantId
     * @return
     */
    @RequestMapping(value = "/merchant/getMerchant")
    public CommonResponse<MerchantDTO> getMerchant(@RequestParam(value = "merchantId") String merchantId) {
        CommonResponse<MerchantDTO> commonResponse = new CommonResponse<>();
        try {

            MerchantDTO merchantDTO = merchantService.getMerchantById(merchantId);
            if (!Optional.ofNullable(merchantDTO).isPresent()) {
                commonResponse.setCode(ResponseCode.PARAMETER_ERROR);
                commonResponse.setMsg("商家不存在");
                return commonResponse;
            }

            merchantDTO = parseCacheMerchantDTO(merchantDTO);

            if (!UserTypeConstant.MERCHANT_TYPE.equals(merchantDTO.getUserType())) {
                commonResponse.setCode(ResponseCode.PARAMETER_ERROR);
                commonResponse.setMsg("非商家用户不许查询");
                return commonResponse;
            }

            // 存储redis位置信息
            Point point = new Point(Double.parseDouble(merchantDTO.getMerchantLongitude() + ""),
                    Double.parseDouble(merchantDTO.getMerchantLatitude() + ""));
            redisTemplate.opsForGeo().add(merchantRedisSetting.getMerchantLac(),
                    new RedisGeoCommands.GeoLocation(merchantDTO.getMerchantId(), point));

            commonResponse.setCode(ResponseCode.SUCCESS);
            commonResponse.setMsg("获取商家信息成功");
            commonResponse.setData(merchantDTO);
        } catch (Exception e) {
            commonResponse.setCode(ResponseCode.SERVER_ERROR);
            commonResponse.setMsg("服务器错误");
            log.error("获取特定商家信息错误:{}", e);
        }
        return commonResponse;
    }

    /**
     * 对内微服务提供的接口调用
     *
     * @param merchantId
     * @return
     */
    @Override
    public Optional<MerchantDTO> getMerchantToInternalUse(@RequestParam(value = "merchantId") String merchantId) {
        MerchantDTO merchantDTO = merchantService.getMerchantById(merchantId);
        merchantDTO = parseCacheMerchantDTO(merchantDTO);
        return Optional.ofNullable(merchantDTO);
    }

    /**
     * 分页获取商家信息
     *
     * @param merchantDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/merchant/getMerchants")
    public PageResponse<MerchantDTO> getMerchants(MerchantDTO merchantDTO,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResponse<MerchantDTO> pageResponse = new PageResponse<>();
        try {
            if (StringUtils.isEmpty(merchantDTO.getMerchantEnable())) {
                merchantDTO.setMerchantEnable(MerchantEnableConstant.MERCHANT_ENABLE);
            }
            PageInfo<MerchantDTO> merchantDTOPageInfo = merchantService.getPageMerchants(merchantDTO, pageNum, pageSize);
            pageResponse.setData(merchantDTOPageInfo);
            pageResponse.setCode(ResponseCode.SUCCESS);
            pageResponse.setMsg("获取商家信息列表成功");
        } catch (Exception e) {
            pageResponse.setCode(ResponseCode.SERVER_ERROR);
            pageResponse.setMsg("服务器错误");
            log.error("分页获取商家信息错误:{}", e);
        }
        return pageResponse;
    }

    /**
     * 删除商家
     *
     * @param merchantDTO
     * @return
     */
    @DeleteMapping(value = "/merchant/deleteMerchant")
    public BaseResponse deleteMerchant(@RequestBody MerchantDTO merchantDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            if (StringUtils.isEmpty(merchantDTO.getMerchantId())) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("商家ID不能为空");
                return baseResponse;
            }
            merchantDTO.setMerchantEnable(MerchantEnableConstant.MERCHANT_DISABLE);
            merchantService.updateMerchantEnable(merchantDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("删除商家信息成功");
        } catch (Exception e) {
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            log.error("删除商家信息错误:{}", e);
        }
        return baseResponse;
    }

    /**
     * 更新商家信息
     *
     * @param merchantDTO
     * @return
     */
    @PutMapping(value = "/merchant/updateMerchant")
    public BaseResponse updateMerchant(@RequestBody MerchantDTO merchantDTO) {
        BaseResponse baseResponse = new BaseResponse();
        Map<String, String> errMap = new HashMap<>();
        try {
            if (StringUtils.isEmpty(merchantDTO.getMerchantId())) {
                errMap.put("merchantId", "商家ID不能为空");
            }

            // 获取修改前的商家信息
            MerchantDTO merchant = merchantService.getMerchantById(merchantDTO.getMerchantId());
            if (!Optional.ofNullable(merchant).isPresent()) {
                errMap.put("merchantId", "商家不存在");
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setErrorMap(errMap);
                return baseResponse;
            }

            merchant = parseCacheMerchantDTO(merchant);

            if (!merchant.getMerchantAddress().equals(merchantDTO.getMerchantAddress())) {
                //验证商家地址
                int validateReturnCode = ValidatorUtil.validParameterAlert(merchantDTO.getMerchantAddress(), "商家地址", true, 1, 150, 4, errMap);
                if (validateReturnCode != 0) {
                    errMap.put("merchantAddress", errMap.get("商家地址"));
                    errMap.remove("商家地址");
                }
                //判断传过来的定位经纬度是否为空，为空需要根据商家地址去转换
                getLatLng(merchantDTO, errMap);
                if (errMap.size() > 0) {
                    baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                    baseResponse.setErrorMap(errMap);
                    return baseResponse;
                }
            }

            String merchantLoginNameOld = merchant.getMerchantLoginName();
            String merchantLoginNameNew = merchantDTO.getMerchantLoginName();

            if (!StringUtils.isEmpty(merchantLoginNameOld)
                    && !StringUtils.isEmpty(merchantLoginNameNew)) {
                if (merchantLoginNameOld.equals(merchantLoginNameNew)) {
                    //merchantDTO.setMerchantLoginName(null);
                } else {
                    MerchantDTO merchantDTO1 = merchantService.loginByMerchantName(merchantDTO);
                    if (Optional.ofNullable(merchantDTO1).isPresent()) {
                        baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                        errMap.put("merchantLoginName", "登录名已存在");
                        baseResponse.setErrorMap(errMap);
                        return baseResponse;
                    }
                }
            }

            String merchantPassword = merchantDTO.getMerchantPassword();
            if(!StringUtils.isEmpty(merchantPassword)){
                String encodePassword = DigestUtils.md5DigestAsHex((merchantPassword + merchant.getSalt()).getBytes());
                merchantDTO.setMerchantPassword(encodePassword);
            }

            merchantService.updateMerchant(merchantDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("更新商家信息成功");
        } catch (Exception e) {
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            log.error("更新商家信息错误:{}", e);
        }
        return baseResponse;
    }

    /**
     * 导出商家信息
     *
     * @param merchantDTO
     * @param httpServletResponse
     */
    @PostMapping(value = "/merchant/export")
    public void exportProduct(@RequestBody MerchantDTO merchantDTO, HttpServletResponse httpServletResponse) {
        try {
            if (StringUtils.isEmpty(merchantDTO.getMerchantEnable())) {
                merchantDTO.setMerchantEnable(MerchantEnableConstant.MERCHANT_ENABLE);
            }
            ExcelUtils.exportExcel(merchantService.getExportMerchants(merchantDTO, 500),
                    "商家信息导出", "商家信息导出", MerchantExportResult.class, "商家信息导出.xlsx", httpServletResponse);
        } catch (Exception e) {
            log.error("导出商家信息错误:{}", e);
        }
    }

    //@RequestMapping(value = "/uploadFiles", method = RequestMethod.POST)
    @Override
    public MultiDataResponse<UploadFileDTO> uploadFiles(HttpServletRequest request) {
        MultiDataResponse<UploadFileDTO> multiDataResponse = new MultiDataResponse<>();
        try {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            Map<String, String> uploadedFiles = uploadFileUtil.uploadFiles(mRequest.getFileMap());
            List<UploadFileDTO> list = new ArrayList<>();
            Iterator<Map.Entry<String, String>> iter = uploadedFiles.entrySet().iterator();
            while (iter.hasNext()) {
                UploadFileDTO uploadFileDTO = new UploadFileDTO();
                Map.Entry<String, String> fileInfo = iter.next();
                System.out.print("Uploaded File Name = " + fileInfo.getKey());
                System.out.println(", Saved Path in Server = " + fileInfo.getValue());
                uploadFileDTO.setFileName(fileInfo.getKey());
                uploadFileDTO.setFilePath(fileInfo.getValue());
                list.add(uploadFileDTO);
            }
            multiDataResponse.setData(list);
            multiDataResponse.setCode(ResponseCode.SUCCESS);
            multiDataResponse.setMsg("上传文件成功");
        } catch (Exception e) {
            multiDataResponse.setCode(ResponseCode.SERVER_ERROR);
            multiDataResponse.setMsg("服务器错误");
            log.error("SERVER_ERROR:{}", e);
        }
        return multiDataResponse;
    }

    @Override
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request,
                                               @RequestParam(value = "filePathName") String filePathName) throws Exception {

        //ServletContext servletContext = request.getServletContext();
        //得到文件所在位置
        //String realPath = servletContext.getRealPath(filePathName);
        //将该文件加入到输入流之中
        InputStream in = new FileInputStream(new File("f:" + filePathName));
        byte[] body = null;
        // 返回下一次对此输入流调用的方法可以不受阻塞地从此输入流读取（或跳过）的估计剩余字节数
        body = new byte[in.available()];
        //读入到输入流里面
        in.read(body);

        //防止中文乱码
        filePathName = new String(filePathName.getBytes("gbk"), "iso8859-1");
        //设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=" + filePathName);
        //设置响应码
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> response = new ResponseEntity<>(body, headers, statusCode);
        return response;
    }

    @GetMapping(value = "/merchant/near")
    public MultiDataResponse getNearMerchant(@RequestParam(value = "lon") double lon,
                                             @RequestParam(value = "lat") double lat,
                                             @RequestParam(value = "distance") double distance) {
        MultiDataResponse multiDataResponse = new MultiDataResponse();
        GeoResults geoResults = merchantService.findByLocationNear(lon, lat, distance);
        multiDataResponse.setData(geoResults.getContent());
        return multiDataResponse;
    }

    private void getLatLng(MerchantDTO merchantDTO, Map<String, String> map) {
        //判断传过来的定位经纬度是否为空，为空需要根据商家地址去转换
        if (!Optional.ofNullable(merchantDTO.getMerchantLatitude()).isPresent()
                || !Optional.ofNullable(merchantDTO.getMerchantLongitude()).isPresent()) {
            BaiDuAddressResult baiDuAddressResult = baiDuMapUtil.getLocation(merchantDTO.getMerchantAddress());
            if (baiDuAddressResult.getStatus() != 0) {
                map.put("latLng", "商家地址错误，无法转换为准确的地图位置");
                return;
            }
            merchantDTO.setMerchantLatitude(baiDuAddressResult.getLocation().getBigDecimal("lat"));
            merchantDTO.setMerchantLongitude(baiDuAddressResult.getLocation().getBigDecimal("lng"));
        }
    }

    /**
     * 验证登录
     *
     * @param merchantDTO
     * @param merchantPassword
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private Map<String, String> validateLogin(MerchantDTO merchantDTO,
                                              String merchantPassword) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Map<String, String> errMap = new HashMap<>();
        if (!Optional.ofNullable(merchantDTO).isPresent()) {
            errMap.put("merchantLoginName", "商家登陆名或密码错误");
            return errMap;
        }

        merchantDTO = parseCacheMerchantDTO(merchantDTO);

        if (!UserTypeConstant.MERCHANT_TYPE.equals(merchantDTO.getUserType())) {
            errMap.put("userType", "非商家用户不许登陆");
            return errMap;
        }

        merchantPassword = RSAUtils.decodePwd(merchantPassword);
        // 验证密码是否正确
        String salt = merchantDTO.getSalt();
        String encodePassword = DigestUtils.md5DigestAsHex((merchantPassword + salt).getBytes());

        if (!merchantDTO.getMerchantPassword().equals(encodePassword)) {
            errMap.put("merchantPassword", "商家登陆名或密码错误");
            return errMap;
        }

        if (MerchantEnableConstant.MERCHANT_DISABLE.equals(merchantDTO.getMerchantEnable())) {
            errMap.put("merchantEnable", "商家已经失效");
        }
        return errMap;
    }

    /**
     * 商家注册验证
     *
     * @param merchantDTO
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private Map<String, String> validateRegister(MerchantDTO merchantDTO) throws InvalidKeySpecException, NoSuchAlgorithmException {
        int validateReturnCode = 0;
        Map<String, String> errorMap = new HashMap<>();

        String merchantLoginName = merchantDTO.getMerchantLoginName();

        // 验证商家登录名
        validateReturnCode = ValidatorUtil.validParameterAlert(merchantLoginName, "商家登录名", true, 1, 200, 4, errorMap);
        if (validateReturnCode != 0) {
            errorMap.put("merchantLoginName", errorMap.get("商家登陆名"));
            errorMap.remove(errorMap.get("商家登陆名"));
        }

        if (clusterClient.exists("merchantLoginName", merchantLoginName)) {
            errorMap.put("merchantLoginName", "商家登录名重复");
        }else {
            MerchantDTO merchant = merchantService.loginByMerchantName(merchantDTO);
            if (Optional.ofNullable(merchant).isPresent()){
                errorMap.put("merchantLoginName", "商家登录名重复");
            }

            clusterClient.add("merchantLoginName", merchantLoginName);
        }



        // 验证商家名字
        validateReturnCode = ValidatorUtil.validParameterAlert(merchantDTO.getMerchantName(), "商家名字", true, 1, 200, 4, errorMap);
        if (validateReturnCode != 0) {
            errorMap.put("merchantName", errorMap.get("商家名字"));
            errorMap.remove(errorMap.get("商家名字"));
        }

        // 验证密码
        validateReturnCode = ValidatorUtil.validParameterAlert(merchantDTO.getMerchantPassword(), "商家密码", true, 1, 15, 4, errorMap);
        if (validateReturnCode != 0) {
            errorMap.put("merchantPassword", errorMap.get("商家密码"));
            errorMap.remove(errorMap.get("商家密码"));
        }

        // 加密密码
        /*String hashPwd = PasswordHash.createHash(merchantDTO.getMerchantPassword());
        merchantDTO.setMerchantPassword(hashPwd);*/

        // 判断商家的座机和手机号，二者不能都为空
        String mobilePhone = merchantDTO.getMerchantMobilephone();
        String telPhone = merchantDTO.getMerchantTelPhone();
        if (ValidatorUtil.isEmpty(mobilePhone) && ValidatorUtil.isEmpty(telPhone)) {
            errorMap.put("phone", "商家的手机和座机不能都为空");
        }
        if (!ValidatorUtil.isEmpty(mobilePhone) && !ValidatorUtil.isMobilePhone(mobilePhone)) {
            errorMap.put("mobilePhone", "商家的手机号码不符合手机格式");
        }
        if (!ValidatorUtil.isEmpty(telPhone) && !ValidatorUtil.isTelPhone(telPhone)) {
            errorMap.put("telPhone", "商家的座机号码必须为(0xx-xxxxxxxx)或者(0xxx-xxxxxxx)的格式");
        }

        // 验证商家地址
        validateReturnCode = ValidatorUtil.validParameterAlert(merchantDTO.getMerchantAddress(), "商家地址", true, 1, 150, 4, errorMap);
        if (validateReturnCode != 0) {
            errorMap.put("merchantAddress", "商家地址");
            errorMap.remove("商家地址");
        }
        //判断传过来的定位经纬度是否为空，为空需要根据商家地址去转换
        getLatLng(merchantDTO, errorMap);
        if (ValidatorUtil.isEmpty(merchantDTO.getMerchantBusinessLicense())) {
            errorMap.put("merchantBusinessLicense", "商家营业执照编码不能为空");
        }
        return errorMap;
    }

    /**
     * 通过redis缓存读取数据时的转换需要
     *
     * @param merchantDTO
     * @return
     */
    private MerchantDTO parseCacheMerchantDTO(MerchantDTO merchantDTO) {
        if (LinkedHashMap.class.isInstance(merchantDTO)) {
            merchantDTO = JSON.parseObject(JSON.toJSON(merchantDTO).toString(), MerchantDTO.class);
        } 
        return merchantDTO;
    }

}
