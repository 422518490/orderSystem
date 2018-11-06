package com.yaya.baseCategories.service;

import com.yaya.baseCategories.dto.BaseSubCategoriesDataInfoDTO;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础小类service接口类
 */
public interface BaseSubCategoriesDataInfoService {

    /**
     * 新增基础小类
     * @param baseSubCategoriesDataInfoDTO
     */
    void addBaseSubCategoriesData(BaseSubCategoriesDataInfoDTO baseSubCategoriesDataInfoDTO);

    /**
     * 获取基础数据
     * @param baseSubCategoriesDataInfoDTO
     * @return
     */
    List<BaseSubCategoriesDataInfoDTO> getCodeAndValueByBatch(BaseSubCategoriesDataInfoDTO baseSubCategoriesDataInfoDTO);

}
