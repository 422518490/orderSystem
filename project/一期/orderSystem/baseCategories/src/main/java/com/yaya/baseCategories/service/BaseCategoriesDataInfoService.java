package com.yaya.baseCategories.service;

import com.yaya.baseCategories.dto.BaseCategoriesDataInfoDTO;

import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础大类service接口类
 */
public interface BaseCategoriesDataInfoService {

    /**
     * 新增基础大类
     * @param baseCategoriesDataInfoDTO
     */
    void addBaseCategoriesData(BaseCategoriesDataInfoDTO baseCategoriesDataInfoDTO);

    /**
     * 删除基础大类
     * @param categoriesDataInfoId
     */
    void deleteBaseCategoriesById(String categoriesDataInfoId);

    /**
     * 根据名字获取信息大类
     * @param categoriesName
     * @return
     */
    Optional<BaseCategoriesDataInfoDTO> getBaseCategoriesByName(String categoriesName);

}
