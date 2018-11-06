package com.yaya.baseCategories.dao;

import com.yaya.baseCategories.dto.BaseCategoriesDataInfoDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础大类mapper接口类
 */
@Mapper
public interface BaseCategoriesDataInfoMapper {

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
    BaseCategoriesDataInfoDTO getBaseCategoriesByName(String categoriesName);

}
