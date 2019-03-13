package com.yaya.orderapi.merchantInterface;

import com.yaya.orderapi.merchantDTO.MerchantDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/21
 * @description 用来实现feign的商家api
 */
public interface MerchantControllerInterface {

    /**
     * 获取特定商家信息内部调用
     *
     * @param merchantId
     * @return
     */
    @GetMapping(value = "/merchant/getMerchantToInternalUse")
    Optional<MerchantDTO> getMerchantToInternalUse(@RequestParam(value = "merchantId") String merchantId);

}
