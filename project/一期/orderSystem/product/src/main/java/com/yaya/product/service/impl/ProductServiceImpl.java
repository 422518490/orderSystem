package com.yaya.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaya.common.constant.OperationTypeConstant;
import com.yaya.common.util.DateUtils;
import com.yaya.common.util.UUIDUtil;
import com.yaya.orderApi.operationLogInterface.OperationLogHandler;
import com.yaya.product.constant.ProductEnableConstant;
import com.yaya.product.dto.ProductDTO;
import com.yaya.product.dao.ProductMapper;
import com.yaya.product.dto.ProductPriceHistoryDTO;
import com.yaya.product.service.ProductPriceHistoryService;
import com.yaya.product.service.ProductService;
import com.yaya.product.template.ProductExportResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/18
 * @description 产品service实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductPriceHistoryService productPriceHistoryService;
    @Autowired
    private OperationLogHandler operationLogHandler;

    @Override
    public void addProduct(ProductDTO productDTO) {
        //生成uuid主键
        String uuid = UUIDUtil.getUUID();
        productDTO.setProductId(uuid);
        productMapper.addProduct(productDTO);
        //发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.MERCHANT_ADD_PRODUCT, productDTO.getLoginUserId(), productDTO.getProductId(), "商家新增产品");
    }

    @Override
    public List<ProductExportResult> getExportProducts(ProductDTO productDTO, int pageSize) {
        List<ProductExportResult> productExportResultList = new ArrayList<>();
        PageInfo<ProductDTO> productDTOPageInfo = null;
        for (int i = 0; i == 0 || i <= (productDTOPageInfo.getTotal() - 1) / pageSize; i++) {
            productDTOPageInfo = getPageProducts(productDTO, i + 1, pageSize);
            productDTOPageInfo.getList().forEach(product -> {
                ProductExportResult productExportResult = new ProductExportResult();
                productExportResult.setProductName(product.getProductName());
                productExportResult.setProductDescription(product.getProductDescription());
                productExportResult.setProductOrderCount(product.getProductOrderCount());
                productExportResult.setProductPrice(product.getProductPrice());
                productExportResult.setMerchantName(productDTO.getMerchantName());
                productExportResult.setCreateTime(DateUtils.dateToStr(product.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
                productExportResultList.add(productExportResult);
            });
        }
        return productExportResultList;
    }

    @Override
    public PageInfo<ProductDTO> getPageProducts(ProductDTO productDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductDTO> productDTOList = productMapper.getProducts(productDTO);
        productDTOList.stream().forEach(product -> {
            product.setCreateTimeStr(DateUtils.dateToStr(product.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
            product.setLastUpdateTimeStr(DateUtils.dateToStr(product.getLastUpdateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
        });
        return new PageInfo<>(productDTOList);
    }

    @Override
    public void updateProductEnable(ProductDTO productDTO) {
        productMapper.updateProductEnable(productDTO);
        //发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.MERCHANT_ENABLE_PRODUCT, productDTO.getLoginUserId(), productDTO.getProductId(), "商家修改产品是否可用:" + productDTO.getProductEnable());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductPrice(ProductDTO productDTO) {
        //获取修改前的数据
        productDTO.setProductEnable(ProductEnableConstant.PRODUCT_ENABLE);
        List<ProductDTO> productDTOList = productMapper.getProducts(productDTO);
        //更新价格
        productMapper.updateProductPrice(productDTO);
        //将原来的价格保存为历史
        ProductPriceHistoryDTO productPriceHistoryDTO = new ProductPriceHistoryDTO();
        productPriceHistoryDTO.setProductPriceHistoryId(UUIDUtil.getUUID());
        productPriceHistoryDTO.setMerchantId(productDTO.getMerchantId());
        productPriceHistoryDTO.setMerchantName(productDTO.getMerchantName());
        productPriceHistoryDTO.setProductId(productDTO.getProductId());
        productPriceHistoryDTO.setProductName(productDTOList.get(0).getProductName());
        productPriceHistoryDTO.setProductPrice(productDTOList.get(0).getProductPrice());
        productPriceHistoryService.addProductPriceHistory(productPriceHistoryDTO);

        //发送日志给rabbit mq
        operationLogHandler.sendOperationLog(OperationTypeConstant.MERCHANT_UPDATE_PRODUCT_PRICE, productDTO.getLoginUserId(), productDTO.getProductId(), "商家更新产品价格");
    }

    @Override
    public Optional<ProductDTO> getProduct(ProductDTO productDTO) {
        productDTO = productMapper.getProduct(productDTO);
        Optional<ProductDTO> productDTOOptional = Optional.ofNullable(productDTO);
        if (productDTOOptional.isPresent()) {
            productDTO.setCreateTimeStr(DateUtils.dateToStr(productDTO.getCreateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
            productDTO.setLastUpdateTimeStr(DateUtils.dateToStr(productDTO.getLastUpdateTime(), DateUtils.DEFAULT_DATETIME_FORMAT));
            return productDTOOptional;
        }
        return Optional.empty();
    }


}
