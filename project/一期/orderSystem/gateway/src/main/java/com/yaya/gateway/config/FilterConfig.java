package com.yaya.gateway.config;

import com.yaya.gateway.filter.AuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/28
 * @description
 */
@Configuration
public class FilterConfig {

    @Bean
    public AuthorizationFilter authorizationFilter(){
        return new AuthorizationFilter();
    }
}
