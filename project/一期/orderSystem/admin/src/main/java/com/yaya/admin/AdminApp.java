package com.yaya.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/8/19
 * @description
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableAdminServer
public class AdminApp {

    public static void main(String[] args) {
        SpringApplication.run(AdminApp.class, args);
    }
}
