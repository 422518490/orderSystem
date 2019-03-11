package com.yaya.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/6
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class CommonApp {

    public static void main(String[] args) {
        SpringApplication.run(CommonApp.class, args);
    }

}
