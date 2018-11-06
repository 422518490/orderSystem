package com.yaya.product.service;

import com.github.pagehelper.PageInfo;
import com.yaya.product.dto.ProductDTO;
import com.yaya.product.template.ProductExportResult;

import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/18
 * @description 产品service接口类
 */
public interface ProductService {

    /**
     * 新增产品信息
     * @param productDTO
     */
    void addProduct(ProductDTO productDTO);

    /**
     * 导出商家的产品信息
     * @param productDTO
     * @param pageSize
     * @return
     */
    List<ProductExportResult> getExportProducts(ProductDTO productDTO,int pageSize);

    /**
     * 分页获取商家的产品信息
     * @param productDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<ProductDTO> getPageProducts(ProductDTO productDTO,Integer pageNum, Integer pageSize);

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
    Optional<ProductDTO> getProduct(ProductDTO productDTO);

}
