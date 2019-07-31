package com.yaya.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/11/23
 * @description
 */
@SpringBootApplication
//@EnableEurekaClient
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class GatewayApp {

    public static void main(String [] args){
        SpringApplication.run(GatewayApp.class,args);
    }
}
