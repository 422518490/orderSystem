package com.yaya.merchant.dao;

import com.yaya.orderApi.merchantDTO.MerchantDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/10/25
 * @description 商家mapper接口扩展类
 */
@Mapper
public interface MerchantMapperExt extends MerchantMapper{

    /**
     * 用户登录
     * @param merchantDTO
     * @return
     */
    MerchantDTO loginByMerchantName(MerchantDTO merchantDTO);

    List<MerchantDTO> findByLocationNear(Point point, Distance distance);
}
