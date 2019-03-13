package com.yaya.operationLog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/26
 * @description 操作日志启动类
 */
@SpringBootApplication(scanBasePackages = "com.yaya")
@EnableDiscoveryClient
@EnableFeignClients
public class OperationLogApp {

    public static void main(String[] args) {
        SpringApplication.run(OperationLogApp.class, args);
    }

}
