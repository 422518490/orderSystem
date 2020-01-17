package com.yaya.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2020/1/14
 * @description
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ClientApp {
    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }
}
