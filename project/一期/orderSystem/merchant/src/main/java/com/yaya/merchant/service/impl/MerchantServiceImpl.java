package com.yaya.merchant.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaya.common.constant.MerchantEnableConstant;
import com.yaya.common.constant.OperationTypeConstant;
import com.yaya.common.constant.UserTypeConstant;
import com.yaya.common.util.DateUtils;
import com.yaya.common.util.SaltUtil;
import com.yaya.common.util.UUIDUtil;
import com.yaya.merchant.dao.MerchantMapperExt;
import com.yaya.merchant.service.MerchantService;
import com.yaya.merchant.setting.MerchantRedisSetting;
import com.yaya.merchant.template.MerchantExportResult;
import com.yaya.orderApi.merchantDTO.MerchantDTO;
import com.yaya.orderApi.merchantModel.Merchant;
import com.yaya.orderApi.merchantModel.MerchantExample;
import com.yaya.orderApi.operationLogInterface.OperationLogHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 商家Service实现类
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Resource
    private MerchantMapperExt merchantMapperExt;

    @Autowired
    private OperationLogHandler operationLogHandler;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MerchantRedisSetting merchantRedisSetting;

    @Override
    // 返回对象作为缓存值
    @Cacheable(cacheManager = "redisCacheManager",
            value = "merchant-r",
            key = "#merchantDTO.merchantLoginName")
    public Optional<MerchantDTO> loginByMerchantName(MerchantDTO merchantDTO) {
        MerchantDTO merchant = merchantMapperExt.loginByMerchantName(merchantDTO);
        return Optional.ofNullable(merchant);
    }

    @Override
    public MerchantDTO merchantRegister(MerchantDTO merchantDTO) {
        // 生成uuid主键
        String uuid = UUIDUtil.getUUID();
        merchantDTO.setMerchantId(uuid);

        // 生成盐值
        String salt = SaltUtil.createRandomCharData(6);
        merchantDTO.setSalt(salt);

        //加密密码
        String encodePassword = DigestUtils.md5DigestAsHex((merchantDTO.getMerchantPassword() + salt).getBytes());
        merchantDTO.setMerchantPassword(encodePassword);

        merchantDTO.setUserType(UserTypeConstant.MERCHANT_TYPE);
        merchantDTO.setMerchantEnable(MerchantEnableConstant.MERCHANT_ENABLE);

        Date date = new Date();
        merchantDTO.setCreateTime(date);
        merchantDTO.setLastUpdateTime(date);

        merchantMapperExt.insertSelective(merchantDTO);

        // 发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.REGISTER_MERCHANT, uuid, uuid, "商家注册");

        // 存储redis位置信息
        Point point = new Point(Double.parseDouble(merchantDTO.getMerchantLongitude() + ""),
                Double.parseDouble(merchantDTO.getMerchantLatitude() + ""));
        redisTemplate.opsForGeo().add(merchantRedisSetting.getMerchantLac(), point, merchantDTO.getMerchantId());

        return merchantDTO;
    }

    @Override
    @Cacheable(cacheManager = "redisCacheManager",
            value = "merchant-r",
            key = "#merchantId",
            condition = "#result ne null")
    public Optional<MerchantDTO> getMerchantById(String merchantId) {
        Merchant merchant = merchantMapperExt.selectByPrimaryKey(merchantId);

        Optional<Merchant> merchantOptional = Optional.ofNullable(merchant);
        if (merchantOptional.isPresent()) {
            MerchantDTO merchantDTO = transform(merchant);
            return Optional.ofNullable(merchantDTO);
        }
        return Optional.empty();
    }

    @Override
    public PageInfo<MerchantDTO> getPageMerchants(MerchantDTO merchantDTO, Integer pageNum, Integer pageSize) {
        List<MerchantDTO> merchantDTOList = new ArrayList<>();

        PageHelper.startPage(pageNum, pageSize);

        MerchantExample merchantExample = new MerchantExample();
        MerchantExample.Criteria criteria = merchantExample.createCriteria();
        criteria.andMerchantEnableEqualTo(merchantDTO.getMerchantEnable());

        String merchantAddress = merchantDTO.getMerchantAddress();
        if (!StringUtils.isEmpty(merchantAddress)) {
            criteria.andMerchantAddressLike(merchantAddress);
        }

        String merchantName = merchantDTO.getMerchantName();
        if (!StringUtils.isEmpty(merchantName)) {
            criteria.andMerchantNameLike(merchantName);
        }

        List<Merchant> merchantList = merchantMapperExt.selectByExample(merchantExample);
        merchantList.stream().forEach(merchant -> {
            MerchantDTO mer = transform(merchant);
            merchantDTOList.add(mer);
        });
        return new PageInfo<>(merchantDTOList);
    }

    @Override
    public List<MerchantExportResult> getExportMerchants(MerchantDTO merchantDTO, int pageSize) {
        List<MerchantExportResult> merchantExportResultList = new ArrayList<>();
        PageInfo<MerchantDTO> merchantDTOPageInfo = null;
        for (int i = 0; i == 0 || i <= (merchantDTOPageInfo.getTotal() - 1) / pageSize; i++) {
            merchantDTOPageInfo = getPageMerchants(merchantDTO, i + 1, pageSize);
            merchantDTOPageInfo.getList().forEach(merchant -> {
                MerchantExportResult merchantExportResult = new MerchantExportResult();
                merchantExportResult.setMerchantName(merchant.getMerchantName());
                merchantExportResult.setMerchantTelPhone(merchant.getMerchantTelPhone());
                merchantExportResult.setMerchantMobilephone(merchant.getMerchantMobilephone());
                merchantExportResult.setMerchantBusinessLicense(merchant.getMerchantBusinessLicense());
                merchantExportResult.setMerchantAddress(merchant.getMerchantAddress());
                merchantExportResult.setCreateTimeStr(DateUtils.dateToStr(merchant.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
                merchantExportResultList.add(merchantExportResult);
            });
        }
        return merchantExportResultList;
    }

    @Override
    @CachePut(cacheManager = "redisCacheManager", value = "merchant-r",
            key = "#merchantDTO.merchantLoginName", condition = "#result ne null")
    public MerchantDTO updateMerchant(MerchantDTO merchantDTO) {
        merchantMapperExt.updateByPrimaryKeySelective(merchantDTO);

        merchantDTO = loginByMerchantName(merchantDTO).get();

        //发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.REGISTER_MERCHANT, merchantDTO.getMerchantId(), merchantDTO.getMerchantId(), "更新商家信息");

        return merchantDTO;
    }

    @Override
    @CacheEvict(cacheManager = "redisCacheManager", value = "merchant-r", key = "#merchantDTO.merchantId")
    public void updateMerchantEnable(MerchantDTO merchantDTO) {
        merchantMapperExt.updateByPrimaryKeySelective(merchantDTO);

        //发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.UPDATE_MERCHANT_ENABLE, merchantDTO.getMerchantId(), merchantDTO.getMerchantId(), "更新商家是否可用状态:" + merchantDTO.getMerchantEnable());
    }

    @Override
    public GeoResults findByLocationNear(double lon,
                                         double lat,
                                         double distanceUnit) {
        // 当前经纬度
        Point point = new Point(lon, lat);
        // 距离的单位
        Metric m = RedisGeoCommands.DistanceUnit.KILOMETERS;
        Distance distance = new Distance(distanceUnit, m);
        Circle circle = new Circle(point, distance);
        // 附加参数
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
        // 添加距离展示
        args.includeDistance();
        // 添加经纬度展示
        args.includeCoordinates();
        // 返回查询数量
        args.limit(10);
        // 排序方式
        args.sortDescending();
        GeoResults radius = redisTemplate.opsForGeo().radius(merchantRedisSetting.getMerchantLac(), circle, args);
        return radius;
    }

    private MerchantDTO transform(Merchant merchant) {
        MerchantDTO merchantDTO = new MerchantDTO();
        BeanUtils.copyProperties(merchant, merchantDTO);

        merchantDTO.setCreateDateTimeStr(DateUtils.dateToStr(merchant.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
        merchantDTO.setLastUpdateDateTimeStr(DateUtils.dateToStr(merchant.getLastUpdateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));

        return merchantDTO;
    }
}
