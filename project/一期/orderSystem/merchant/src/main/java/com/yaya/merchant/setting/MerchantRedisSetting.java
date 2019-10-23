package com.yaya.merchant.setting;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/10/23
 * @description
 */
@Configuration
@Data
public class MerchantRedisSetting {

    @Value("${merchant.redis.geo.merchant-loc}")
    private String merchantLac;
}
