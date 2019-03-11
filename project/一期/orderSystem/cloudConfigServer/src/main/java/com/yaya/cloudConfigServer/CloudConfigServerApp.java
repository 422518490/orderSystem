package com.yaya.cloudconfigserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/10/18
 * @description 配置服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class CloudConfigServerApp {

    public static void main(String[] args) {
        SpringApplication.run(CloudConfigServerApp.class, args);
    }

    @RestController
    @RefreshScope
    static class CloudConfigServerController {
        @Value("${test_name}")
        private String name;

        @Value("${test_address}")
        private String address;

        @Value("${test_version}")
        private String version;

        @Value("${test_application_name}")
        private String applicationName;

        @RequestMapping("/test")
        public String test(){
            return "你好，我是"+name+",年龄："+address+"地址。当前环境："+version +";applicationName是:" + applicationName;
        }
    }
}
