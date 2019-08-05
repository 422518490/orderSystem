package com.yaya.common.config;

import io.rebloom.client.Client;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/23
 * @description redis bloom过滤器
 */
@Configuration
@Data
public class RedisBloomConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Bean
    public Client bloomClient(){
        Client client = new Client(redisHost,redisPort);
        return client;
    }
}
