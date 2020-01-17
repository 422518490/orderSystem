package com.yaya.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2020/1/16
 * @description
 */
@Configuration
public class RetryConfig {

    /**
     * 自定义该bean进行retry控制
     * @return
     */
    @Bean
    public RetryOperationsInterceptor configServerRetryInterceptor(){
        return new RetryOperationsInterceptor();
    }
}
