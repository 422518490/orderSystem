package com.yaya.eurekaServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/14
 * @description
 */
@SpringBootApplication
@EnableEurekaServer
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class EurekaServerApp {

    public static void main(String [] args){
        SpringApplication.run(EurekaServerApp.class,args);
    }

}
