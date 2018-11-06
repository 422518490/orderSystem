package com.yaya.product.dao;

import com.yaya.product.dto.ProductPriceHistoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/21
 * @description 产品历史价格mapper接口类
 */
@Mapper
public interface ProductPriceHistoryMapper {

    /**
     * 新增产品价格历史信息
     * @param productPriceHistoryDTO
     */
    void addProductPriceHistory(ProductPriceHistoryDTO productPriceHistoryDTO);

    /**
     * 获取商家历史产品的历史价格
     * @param productPriceHistoryDTO
     * @return
     */
    List<ProductPriceHistoryDTO> getProductPriceHistorys(ProductPriceHistoryDTO productPriceHistoryDTO);

}
