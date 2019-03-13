package com.yaya.discount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/17
 * @description 产品启动类
 */
@SpringBootApplication(scanBasePackages = "com.yaya")
@EnableDiscoveryClient
@EnableFeignClients
public class DiscountApp {

    public static void main(String[] args) {
        SpringApplication.run(DiscountApp.class, args);
    }

}
