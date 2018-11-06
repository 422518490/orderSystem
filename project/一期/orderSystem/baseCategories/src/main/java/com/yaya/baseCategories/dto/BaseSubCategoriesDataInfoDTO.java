package com.yaya.baseCategories.dto;

import com.yaya.baseCategories.model.BaseSubCategoriesDataInfo;
import lombok.Data;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础小类扩展类
 */
@Data
public class BaseSubCategoriesDataInfoDTO extends BaseSubCategoriesDataInfo {

    //编码字符串
    private String categoriesCodeStr;
    //编码字符串集合
    private List<String> categoriesCodeList;
    //大类名称
    private String categoriesName ;

    //小类对象集合
    private List<BaseSubCategoriesDataInfoDTO> baseSubCategoriesDataInfoDTOList;

}
