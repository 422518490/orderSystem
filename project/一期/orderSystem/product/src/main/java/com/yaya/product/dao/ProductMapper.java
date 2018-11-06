package com.yaya.product.dao;

import com.yaya.product.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/18
 * @description 产品mapper接口类
 */
@Mapper
public interface ProductMapper {

    /**
     * 新增产品信息
     * @param productDTO
     */
    void addProduct(ProductDTO productDTO);

    /**
     * 获取商家的产品信息
     * @param productDTO
     * @return
     */
    List<ProductDTO> getProducts(ProductDTO productDTO);

    /**
     * 更新产品的状态
     * @param productDTO
     */
    void updateProductEnable(ProductDTO productDTO);

    /**
     * 更新产品价格
     * @param productDTO
     */
    void updateProductPrice(ProductDTO productDTO);

    /**
     * 获取单个产品信息
     * @param productDTO
     * @return
     */
    ProductDTO getProduct(ProductDTO productDTO);

}
