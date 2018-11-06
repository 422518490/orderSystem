package com.yaya.cloudConfigServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/10/18
 * @description 配置服务启动类
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class CloudConfigServerApp {

    public static void main(String [] args){
        SpringApplication.run(CloudConfigServerApp.class,args);
    }
}
