package com.yaya.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/9
 * @description 设置自定义redis属性
 */
@PropertySource(ignoreResourceNotFound=true,value= "classpath:config/application-common.properties")
@Component
public class SetSelfRedis {

    // 会话失效时间
    @Value("${set.redis.sessionInvalid}")
    private String sessionInvalid;

    public String getSessionInvalid() {
        return sessionInvalid;
    }

}
