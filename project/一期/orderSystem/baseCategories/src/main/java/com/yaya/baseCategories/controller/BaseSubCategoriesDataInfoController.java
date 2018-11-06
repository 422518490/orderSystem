package com.yaya.baseCategories.controller;

import com.yaya.baseCategories.dto.BaseSubCategoriesDataInfoDTO;
import com.yaya.baseCategories.service.BaseSubCategoriesDataInfoService;
import com.yaya.common.response.BaseResponse;
import com.yaya.common.response.MultiDataResponse;
import com.yaya.common.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/29
 * @description 基础小类的controller类
 */
@RestController
@Slf4j
public class BaseSubCategoriesDataInfoController {

    @Autowired
    private BaseSubCategoriesDataInfoService baseSubCategoriesDataInfoService;

    /**
     * 新增基础信息
     *
     * @param baseSubCategoriesDataInfoDTO
     * @return
     */
    @PostMapping(value = "/baseCategories/addBaseCategoriesData")
    public BaseResponse addBaseCategoriesData(@RequestBody BaseSubCategoriesDataInfoDTO baseSubCategoriesDataInfoDTO) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Map<String, String> errMap = new HashMap<>();

            if (StringUtils.isEmpty(baseSubCategoriesDataInfoDTO.getCategoriesName())) {
                errMap.put("categoriesName","大类名称不能为空");
            }

            List<BaseSubCategoriesDataInfoDTO> baseSubCategoriesDataInfoDTOs = baseSubCategoriesDataInfoDTO.getBaseSubCategoriesDataInfoDTOList();
            if (baseSubCategoriesDataInfoDTOs == null
                    || baseSubCategoriesDataInfoDTOs.size() == 0) {
                errMap.put("baseSubCategoriesDataInfoDTOList","小类不能为空");
            }

            if (errMap.size() > 0) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setErrorMap(errMap);
                return baseResponse;
            }

            for (BaseSubCategoriesDataInfoDTO subCategoriesDataInfoDTO : baseSubCategoriesDataInfoDTOs) {
                if (StringUtils.isEmpty(subCategoriesDataInfoDTO.getCategoriesCode())) {
                    baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                    baseResponse.setMsg("小类名称为空");
                    return baseResponse;
                }
                if (StringUtils.isEmpty(subCategoriesDataInfoDTO.getCategoriesValue())) {
                    baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                    baseResponse.setMsg("小类值为空");
                    return baseResponse;
                }
            }

            baseSubCategoriesDataInfoService.addBaseSubCategoriesData(baseSubCategoriesDataInfoDTO);
            baseResponse.setCode(ResponseCode.SUCCESS);
            baseResponse.setMsg("新增基础信息成功");
        } catch (Exception e) {
            log.error("新增基础信息错误:{}", e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
        }
        return baseResponse;
    }

    /**
     * 根据大类获取基础信息
     *
     * @param baseSubCategoriesDataInfoDTO
     * @return
     */
    @GetMapping(value = "/baseCategories/getCategoriesDataByName")
    public MultiDataResponse<BaseSubCategoriesDataInfoDTO> getCategoriesDataByName(BaseSubCategoriesDataInfoDTO baseSubCategoriesDataInfoDTO) {
        MultiDataResponse<BaseSubCategoriesDataInfoDTO> multiDataResponse = new MultiDataResponse();
        try {
            if (StringUtils.isEmpty(baseSubCategoriesDataInfoDTO.getCategoriesName())) {
                multiDataResponse.setCode(ResponseCode.PARAMETER_ERROR);
                multiDataResponse.setMsg("大类名称不能为空");
                return multiDataResponse;
            }
            List<BaseSubCategoriesDataInfoDTO> baseSubCategoriesDataInfoDTOs = baseSubCategoriesDataInfoService.getCodeAndValueByBatch(baseSubCategoriesDataInfoDTO);
            multiDataResponse.setData(baseSubCategoriesDataInfoDTOs);
            multiDataResponse.setCode(ResponseCode.SUCCESS);
            multiDataResponse.setMsg("获取基础信息成功");
        } catch (Exception e) {
            log.error("获取基础信息错误:{}", e);
            multiDataResponse.setCode(ResponseCode.SERVER_ERROR);
            multiDataResponse.setMsg("服务器错误");
        }
        return multiDataResponse;
    }

}
