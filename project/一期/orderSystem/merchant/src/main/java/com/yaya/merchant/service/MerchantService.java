package com.yaya.merchant.service;

import com.github.pagehelper.PageInfo;
import com.yaya.merchant.template.MerchantExportResult;
import com.yaya.orderapi.merchantDTO.MerchantDTO;

import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 商家Service接口类
 */
public interface MerchantService {

    /**
     * 根据商家登陆获取商家信息
     * @param merchantDTO
     * @return
     */
    Optional<MerchantDTO> loginByMerchantName(MerchantDTO merchantDTO);

    /**
     * 商家注册
     * @param merchantDTO
     * @return
     */
    MerchantDTO merchantRegister(MerchantDTO merchantDTO);

    /**
     * 根据merchantId获取商家信息
     * @param merchantId
     * @return
     */
    Optional<MerchantDTO> getMerchantById(String merchantId);

    /**
     * 获取所有商家信息
     * @param merchantDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<MerchantDTO> getPageMerchants(MerchantDTO merchantDTO, Integer pageNum, Integer pageSize);

    /**
     * 导出商家信息
     * @param merchantDTO
     * @param pageSize
     * @return
     */
    List<MerchantExportResult> getExportMerchants(MerchantDTO merchantDTO, int pageSize);

    /**
     * 更新商家信息
     * @param merchantDTO
     */
    void updateMerchant(MerchantDTO merchantDTO);

    /**
     * 更新商家可用状态
     * @param merchantDTO
     */
    void updateMerchantEnable(MerchantDTO merchantDTO);
}
