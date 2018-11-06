package com.yaya.baseCategories.dao;

import com.yaya.baseCategories.dto.BaseSubCategoriesDataInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础小类mapper接口类
 */
@Mapper
public interface BaseSubCategoriesDataInfoMapper {

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

    /**
     * 根据大类ID删除基础小类信息
     * @param categoriesDataInfoId
     */
    void deleteBaseSubCategories(String categoriesDataInfoId);
}
