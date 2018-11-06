package com.yaya.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yaya.product.dao.ProductPriceHistoryMapper;
import com.yaya.product.dto.ProductPriceHistoryDTO;
import com.yaya.product.service.ProductPriceHistoryService;
import com.yaya.product.template.ProductPriceHisExportResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/21
 * @description 产品历史价格service实现类
 */
@Service
public class ProductPriceHistoryServiceImpl implements ProductPriceHistoryService {

    @Autowired
    private ProductPriceHistoryMapper productPriceHistoryMapper;

    @Override
    public void addProductPriceHistory(ProductPriceHistoryDTO productPriceHistoryDTO) {
        productPriceHistoryMapper.addProductPriceHistory(productPriceHistoryDTO);
    }

    @Override
    public PageInfo<ProductPriceHistoryDTO> getPageProductPriceHis(ProductPriceHistoryDTO productPriceHistoryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductPriceHistoryDTO> productPriceHistoryDTOList = productPriceHistoryMapper.getProductPriceHistorys(productPriceHistoryDTO);
        return new PageInfo<>(productPriceHistoryDTOList);
    }

    @Override
    public List<ProductPriceHisExportResult> getExportProductPriceHis(ProductPriceHistoryDTO productPriceHistoryDTO, int pageSize) {
        List<ProductPriceHisExportResult> productPriceHisExportResults = new ArrayList<>();
        PageInfo<ProductPriceHistoryDTO> productPriceHistoryDTOPageInfo = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i == 0 || i <= (productPriceHistoryDTOPageInfo.getTotal() - 1) / pageSize; i++) {
            productPriceHistoryDTOPageInfo= getPageProductPriceHis(productPriceHistoryDTO,i+1,pageSize);
            productPriceHistoryDTOPageInfo.getList().forEach(productPriceHistory -> {
                ProductPriceHisExportResult productExportResult = new ProductPriceHisExportResult();
                productExportResult.setProductName(productPriceHistory.getProductName());
                productExportResult.setProductPrice(productPriceHistory.getProductPrice());
                productExportResult.setMerchantName(productPriceHistory.getMerchantName());
                productExportResult.setCreateTime(simpleDateFormat.format(productPriceHistory.getCreateTime()));
                productPriceHisExportResults.add(productExportResult);
            });
        }
        return productPriceHisExportResults;
    }
}
