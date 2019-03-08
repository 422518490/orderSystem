package com.yaya.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 商家启动类
 */
@SpringBootApplication(scanBasePackages = "com.yaya")
@EnableDiscoveryClient
@EnableFeignClients
public class MerchantApp {

    public static void main(String[] args) {
        SpringApplication.run(MerchantApp.class, args);
    }
/*
    @Bean
    public ContextRefresher createContextRefresher(ConfigurableApplicationContext context, RefreshScope scope){
        return new ContextRefresher(context,scope);
    }*/
}
