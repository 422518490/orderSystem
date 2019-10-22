package com.yaya.order.setting;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/10/22
 * @description
 */
@Configuration
@Data
public class ListenerSetting {

    /**
     * 订单的过期监听
     */
    @Value("${listener.redis.order_topic_expired}")
    private String orderTopicExpired;
}
