package com.yaya.nacoskaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/14
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class NacosServerApp {

    public static void main(String[] args) {
        SpringApplication.run(NacosServerApp.class, args);
    }

}
