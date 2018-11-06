package com.yaya.product.service;

import com.github.pagehelper.PageInfo;
import com.yaya.product.dto.ProductPriceHistoryDTO;
import com.yaya.product.template.ProductPriceHisExportResult;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/21
 * @description 产品历史价格service接口类
 */
public interface ProductPriceHistoryService {

    /**
     * 新增产品价格历史信息
     * @param productPriceHistoryDTO
     */
    void addProductPriceHistory(ProductPriceHistoryDTO productPriceHistoryDTO);


    /**
     * 分页获取商家历史产品的历史价格
     * @param productPriceHistoryDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ProductPriceHistoryDTO> getPageProductPriceHis(ProductPriceHistoryDTO productPriceHistoryDTO, Integer pageNum, Integer pageSize);

    /**
     * 导出商家历史产品的历史价格
     * @param productPriceHistoryDTO
     * @param pageSize
     * @return
     */
    List<ProductPriceHisExportResult> getExportProductPriceHis(ProductPriceHistoryDTO productPriceHistoryDTO, int pageSize);

}
