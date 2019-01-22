package com.yaya.common.setting;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/5/30
 * @description redis基础配置
 */
@Configuration
@Data
public class RedisSetting {
    /*@Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.keytimeout}")
    private long keytimeout;

    @Value("${spring.redis.database}")
    private int dataBase;

    @Value("${spring.redis.password}")
    private String password;*/
}
