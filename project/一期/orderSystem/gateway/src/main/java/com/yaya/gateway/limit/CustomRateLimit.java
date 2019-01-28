package com.yaya.gateway.limit;

import com.yaya.gateway.properties.GatewayLimitProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/28
 * @description 自定义限流相关
 */
@Configuration
@Slf4j
public class CustomRateLimit {

    /**
     * 自定义redis令牌桶参数
     *
     * @param gatewayLimitProperties
     * @return
     */
    @Bean(name = "customRedisRateLimiter")
    public RedisRateLimiter customRedisRateLimiter(GatewayLimitProperties gatewayLimitProperties) {
        GatewayLimitProperties.RedisRate redisRate = gatewayLimitProperties.getRedisRate();
        if (Optional.ofNullable(redisRate).isPresent()) {
            throw new RuntimeException("配置未初始化");
        }
        return new RedisRateLimiter(redisRate.getReplenishRate(), redisRate.getBurstCapacity());
    }

    @Bean(name = RemoteAddressKeyResolver.BEAN_NAME)
    public RemoteAddressKeyResolver remoteAddressKeyResolver(){
        return new RemoteAddressKeyResolver();
    }
}
