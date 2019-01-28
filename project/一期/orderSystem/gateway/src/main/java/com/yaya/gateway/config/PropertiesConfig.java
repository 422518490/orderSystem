package com.yaya.gateway.config;

import com.yaya.gateway.properties.GatewayLimitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/28
 * @description
 */
@Configuration
public class PropertiesConfig {

    @Bean(name = "gatewayLimitProperties")
    @ConfigurationProperties(prefix = "gateway.limit")
    public GatewayLimitProperties gatewayLimitProperties() {
        return new GatewayLimitProperties();
    }
}
