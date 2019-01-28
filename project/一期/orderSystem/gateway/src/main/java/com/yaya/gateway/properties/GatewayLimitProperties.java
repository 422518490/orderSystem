package com.yaya.gateway.properties;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/28
 * @description redis令牌桶token配置
 */
@Data
public class GatewayLimitProperties {

    /**
     * redis令牌桶配置
     */
    private RedisRate redisRate;

    private Throttle throttle;


    @Data
    public static class RedisRate{
        /**
         * 每个用户允许的请求数
         */
        private Integer replenishRate;

        /**
         * 允许的最大并发数
         */
        private Integer burstCapacity;
    }

    @Data
    public static class Throttle{
        int capacity;

        int refillTokens;

        int refillPeriod;

        TimeUnit refillUnit = TimeUnit.MILLISECONDS;
    }
}
