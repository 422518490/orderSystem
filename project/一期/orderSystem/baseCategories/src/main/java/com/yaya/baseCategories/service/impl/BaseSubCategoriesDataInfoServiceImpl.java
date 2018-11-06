package com.yaya.baseCategories.service.impl;

import com.yaya.baseCategories.dao.BaseSubCategoriesDataInfoMapper;
import com.yaya.baseCategories.dto.BaseCategoriesDataInfoDTO;
import com.yaya.baseCategories.dto.BaseSubCategoriesDataInfoDTO;
import com.yaya.baseCategories.service.BaseCategoriesDataInfoService;
import com.yaya.baseCategories.service.BaseSubCategoriesDataInfoService;
import com.yaya.common.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础小类service实现类
 */
@Service
public class BaseSubCategoriesDataInfoServiceImpl implements BaseSubCategoriesDataInfoService {

    @Autowired
    private BaseSubCategoriesDataInfoMapper baseSubCategoriesDataInfoMapper;
    @Autowired
    private BaseCategoriesDataInfoService baseCategoriesDataInfoService;

    @Override
    @Transactional
    public void addBaseSubCategoriesData(BaseSubCategoriesDataInfoDTO baseSubCategoriesDataInfoDTO) {
        //查看是否已经存在该名称的大类信息
        Optional<BaseCategoriesDataInfoDTO> baseCategoriesDataInfoDTOOptional = baseCategoriesDataInfoService.getBaseCategoriesByName(baseSubCategoriesDataInfoDTO.getCategoriesName());
        //不为空删除原来的信息重新插入
        if(baseCategoriesDataInfoDTOOptional.isPresent()){
            String categoriesDataInfoId = baseCategoriesDataInfoDTOOptional.get().getCategoriesDataInfoId();
            baseCategoriesDataInfoService.deleteBaseCategoriesById(categoriesDataInfoId);
            baseSubCategoriesDataInfoMapper.deleteBaseSubCategories(categoriesDataInfoId);
        }
        BaseCategoriesDataInfoDTO baseCategoriesDataInfoDTO = new BaseCategoriesDataInfoDTO();
        baseCategoriesDataInfoDTO.setCategoriesNam(baseSubCategoriesDataInfoDTO.getCategoriesName());
        String uuid = UUIDUtil.getUUID();
        baseCategoriesDataInfoDTO.setCategoriesDataInfoId(uuid);
        baseCategoriesDataInfoService.addBaseCategoriesData(baseCategoriesDataInfoDTO);
        baseSubCategoriesDataInfoDTO.setSubCategoriesDataInfoId(uuid);
        baseSubCategoriesDataInfoMapper.addBaseSubCategoriesData(baseSubCategoriesDataInfoDTO);
    }

    @Override
    public List<BaseSubCategoriesDataInfoDTO> getCodeAndValueByBatch(BaseSubCategoriesDataInfoDTO baseSubCategoriesDataInfoDTO) {
        List<BaseSubCategoriesDataInfoDTO> dataInfoList = baseSubCategoriesDataInfoMapper.getCodeAndValueByBatch(baseSubCategoriesDataInfoDTO);
        return dataInfoList;
    }
}
