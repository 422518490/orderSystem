package com.yaya.baseCategories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/3/15
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients
@RefreshScope
public class BaseCategoriesApp {

    public static void main(String[] args) {
        SpringApplication.run(BaseCategoriesApp.class, args);
    }
}
