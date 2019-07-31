package com.yaya.product.controller;

import com.github.pagehelper.PageInfo;
import com.yaya.common.constant.MerchantEnableConstant;
import com.yaya.common.response.BaseResponse;
import com.yaya.common.response.CommonResponse;
import com.yaya.common.response.PageResponse;
import com.yaya.common.response.ResponseCode;
import com.yaya.common.util.ExcelUtils;
import com.yaya.orderApi.merchantDTO.MerchantDTO;
import com.yaya.product.constant.ProductEnableConstant;
import com.yaya.product.dto.ProductDTO;
import com.yaya.product.orderApi.MerchantInterface;
import com.yaya.product.service.ProductService;
import com.yaya.product.template.ProductExportResult;
import io.rebloom.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/17
 * @description 产品controller 类
 */
@RestController
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MerchantInterface merchantInterface;

    @Resource
    private Client bloomClient;

    /**
     * 添加商家产品
     *
     * @param productDTO
     * @return
     */
    @PostMapping(value = "/product/addProduct")
    public BaseResponse addProduct(@RequestBody ProductDTO productDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Map<String, String> errMap = new HashMap<>();
            BigDecimal productPrice = productDTO.getProductPrice();

            if (StringUtils.isEmpty(productDTO.getProductName())) {
                errMap.put("productName", "产品名称不能为空");
            }

            if (StringUtils.isEmpty(productDTO.getMerchantId())) {
                errMap.put("merchantId", "商家ID不能为空");
            }

            if (productPrice == null || productPrice.compareTo(new BigDecimal(0)) == 0) {
                errMap.put("productPrice", "产品价格不能为空");
            }

            if (errMap.size() > 0) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setErrorMap(errMap);
                return baseResponse;
            }

            if (bloomClient.exists("product",productDTO.getProductName())){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("产品已经存在，不能重复添加");
                return baseResponse;
            }

            productService.addProduct(productDTO);
            bloomClient.add("product",productDTO.getProductName());
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("新增产品成功");
        } catch (Exception e) {
            log.error("新增产品错误:{}", e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
        }
        return baseResponse;
    }

    /**
     * 分页获取商家的产品信息
     *
     * @param productDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/product/getPageProducts")
    public PageResponse<ProductDTO> getPageProducts(ProductDTO productDTO,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResponse<ProductDTO> pageResponse = new PageResponse<>();
        try {
            if (StringUtils.isEmpty(productDTO.getMerchantId())) {
                pageResponse.setCode(ResponseCode.PARAMETER_ERROR);
                pageResponse.setMsg("商家ID不能为空");
                return pageResponse;
            }

            isProductEnable(productDTO);

            PageInfo<ProductDTO> productDTOPageInfo = productService.getPageProducts(productDTO, pageNum, pageSize);
            pageResponse.setData(productDTOPageInfo);
            pageResponse.setCode(ResponseCode.SUCCESS);
            pageResponse.setMsg("获取商家产品信息列表成功");
        } catch (Exception e) {
            log.error("分页获取商家产品信息错误:{}", e);
            pageResponse.setCode(ResponseCode.SERVER_ERROR);
            pageResponse.setMsg("服务器错误");
        }
        return pageResponse;
    }

    /**
     * 获取单个商家产品信息
     *
     * @param productDTO
     * @return
     */
    @GetMapping(value = "/product/getProduct")
    public CommonResponse<ProductDTO> getProduct(ProductDTO productDTO) {
        CommonResponse<ProductDTO> commonResponse = new CommonResponse<>();
        try {
            Map<String, String> errMap = new HashMap<>();

            String merchantId = productDTO.getMerchantId();
            if (StringUtils.isEmpty(merchantId)) {
                errMap.put("merchantId", "商家ID不能为空");
            }

            if (StringUtils.isEmpty(productDTO.getProductId())) {
                errMap.put("productId", "产品ID不能为空");
            }

            if (errMap.size() > 0) {
                commonResponse.setCode(ResponseCode.PARAMETER_ERROR);
                commonResponse.setErrorMap(errMap);
                return commonResponse;
            }

            isProductEnable(productDTO);
            Optional<ProductDTO> productDTOOptional = productService.getProduct(productDTO);
            if (!productDTOOptional.isPresent()) {
                commonResponse.setCode(ResponseCode.PARAMETER_ERROR);
                commonResponse.setMsg("产品不存在");
                return commonResponse;
            }

            Optional<MerchantDTO> merchantDTOOptional = merchantInterface.getMerchantToInternalUse(merchantId);
            commonResponse = (CommonResponse<ProductDTO>) isMerchantExists(merchantDTOOptional, productDTOOptional.get());

            if (commonResponse.getCode() != ResponseCode.SUCCESS) {
                return commonResponse;
            }
            commonResponse.setData(productDTOOptional.get());
            commonResponse.setCode(ResponseCode.SUCCESS);
            commonResponse.setMsg("获取商家产品信息列表成功");
        } catch (Exception e) {
            log.error("获取商家产品信息错误:{}", e);
            commonResponse.setCode(ResponseCode.SERVER_ERROR);
            commonResponse.setMsg("服务器错误");
        }
        return commonResponse;
    }

    /**
     * 导出商家的产品
     *
     * @param productDTO
     * @param httpServletResponse
     */
    @PostMapping(value = "/product/export")
    public void exportProduct(@RequestBody ProductDTO productDTO, HttpServletResponse httpServletResponse) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            String merchantId = productDTO.getMerchantId();
            /*if(StringUtils.isEmpty(merchantId)){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("商家ID不能为空");
                return baseResponse;
            }*/
            isProductEnable(productDTO);
            Optional<MerchantDTO> merchantDTOOptional = merchantInterface.getMerchantToInternalUse(merchantId);
            baseResponse = isMerchantExists(merchantDTOOptional, productDTO);
            if (baseResponse.getCode() != ResponseCode.SUCCESS) {
                log.error("导出商家产品信息错误:商家不存在");
                return;
            }
            ExcelUtils.exportExcel(productService.getExportProducts(productDTO, 500),
                    "商家产品导出", "商家产品导出", ProductExportResult.class, "商家产品导出.xlsx", httpServletResponse);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("商家产品导出成功");
        } catch (Exception e) {
            log.error("导出商家产品信息错误:{}", e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
        }
        //return baseResponse;
    }

    /**
     * 商家删除产品
     *
     * @param productDTO
     * @return
     */
    @DeleteMapping(value = "/product/deleteProduct")
    public BaseResponse deleteProduct(@RequestBody ProductDTO productDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Map<String, String> errMap = new HashMap<>();

            List<String> productIdList = new ArrayList<>();
            String productIdStr = productDTO.getProductIdStr();
            if (StringUtils.isEmpty(productDTO.getMerchantId())) {
                errMap.put("merchantId", "商家ID不能为空");
            }

            if (StringUtils.isEmpty(productIdStr)) {
                errMap.put("productIdStr", "产品ID不能为空");
            }

            if (errMap.size() > 0) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setErrorMap(errMap);
                return baseResponse;
            }

            String[] productId = productIdStr.split(",");
            for (int i = 0; i == 0 || i < productId.length - 1; i++) {
                productIdList.add(productId[i]);
            }

            productDTO.setProductIdList(productIdList);
            productDTO.setProductEnable(ProductEnableConstant.PRODUCT_DISABLE);
            productService.updateProductEnable(productDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("删除产品成功");
        } catch (Exception e) {
            log.error("删除产品错误:{}", e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
        }
        return baseResponse;
    }

    /**
     * 更新商家产品价格
     *
     * @param productDTO
     * @return
     */
    @PostMapping(value = "/product/updateProductPrice")
    public BaseResponse updateProductPrice(@RequestBody ProductDTO productDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Map<String, String> errMap = new HashMap<>();

            BigDecimal productPrice = productDTO.getProductPrice();
            String merchantId = productDTO.getMerchantId();
            if (StringUtils.isEmpty(merchantId)) {
                errMap.put("merchantId", "商家ID不能为空");
            }

            if (StringUtils.isEmpty(productDTO.getProductId())) {
                errMap.put("productId", "产品ID不能为空");
            }

            if (productPrice == null || productPrice.compareTo(new BigDecimal(0)) == 0) {
                errMap.put("productPrice", "产品价格不能为空");
            }

            if (errMap.size() > 0) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setErrorMap(errMap);
                return baseResponse;
            }

            Optional<MerchantDTO> merchantDTOOptional = merchantInterface.getMerchantToInternalUse(merchantId);
            baseResponse = isMerchantExists(merchantDTOOptional, productDTO);
            if (baseResponse.getCode() != ResponseCode.SUCCESS) {
                return baseResponse;
            }

            productService.updateProductPrice(productDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("更新产品价格成功");
        } catch (Exception e) {
            log.error("更新产品价格错误:{}", e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
        }
        return baseResponse;
    }

    /**
     * 判断是否有产品是否可用值传入
     *
     * @param productDTO
     */
    private void isProductEnable(ProductDTO productDTO) {
        String productEnable = productDTO.getProductEnable();
        if (StringUtils.isEmpty(productEnable)) {
            productDTO.setProductEnable(ProductEnableConstant.PRODUCT_ENABLE);
        }
    }

    private BaseResponse isMerchantExists(Optional<MerchantDTO> merchantDTOOptional, ProductDTO productDTO) {
        BaseResponse baseResponse = new BaseResponse();
        if (merchantDTOOptional.isPresent()) {
            if (MerchantEnableConstant.MERCHANT_DISABLE.equals(merchantDTOOptional.get().getMerchantEnable())) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("商家不存在！");
                return baseResponse;
            }
            productDTO.setMerchantName(merchantDTOOptional.get().getMerchantName());
        }
        baseResponse.setCode(ResponseCode.SUCCESS);
        return baseResponse;
    }

}
