package com.yaya.cloudConfigServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/10/18
 * @description 配置服务启动类
 */
@SpringBootApplication
@EnableConfigServer
public class CloudConfigServerApp {

    public static void main(String[] args) {
        SpringApplication.run(CloudConfigServerApp.class, args);
    }
}
