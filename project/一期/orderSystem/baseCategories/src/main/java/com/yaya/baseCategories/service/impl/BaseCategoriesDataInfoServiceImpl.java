package com.yaya.baseCategories.service.impl;

import com.yaya.baseCategories.dao.BaseCategoriesDataInfoMapper;
import com.yaya.baseCategories.dto.BaseCategoriesDataInfoDTO;
import com.yaya.baseCategories.service.BaseCategoriesDataInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础大类service实现类
 */
@Service
public class BaseCategoriesDataInfoServiceImpl implements BaseCategoriesDataInfoService {

    @Autowired
    private BaseCategoriesDataInfoMapper baseCategoriesDataInfoMapper;

    @Override
    public void addBaseCategoriesData(BaseCategoriesDataInfoDTO baseCategoriesDataInfoDTO) {
        baseCategoriesDataInfoMapper.addBaseCategoriesData(baseCategoriesDataInfoDTO);
    }

    @Override
    public void deleteBaseCategoriesById(String categoriesDataInfoId) {
        baseCategoriesDataInfoMapper.deleteBaseCategoriesById(categoriesDataInfoId);
    }

    @Override
    public Optional<BaseCategoriesDataInfoDTO> getBaseCategoriesByName(String categoriesName) {
        BaseCategoriesDataInfoDTO baseCategoriesDataInfoDTO
                = baseCategoriesDataInfoMapper.getBaseCategoriesByName(categoriesName);
        return Optional.ofNullable(baseCategoriesDataInfoDTO).isPresent()
                ?Optional.ofNullable(baseCategoriesDataInfoDTO):Optional.empty();
    }
}
