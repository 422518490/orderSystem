package com.yaya.product.orderApi;

import com.yaya.orderApi.merchantInterface.MerchantControllerInterface;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/21
 * @description
 */
@FeignClient(value = "merchant")
public interface MerchantInterface extends MerchantControllerInterface {
}
