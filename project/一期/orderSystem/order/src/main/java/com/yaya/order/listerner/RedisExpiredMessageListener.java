package com.yaya.order.listerner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/8/3
 * @description redis消息过期监听处理类
 */
@Component
@Slf4j
public class RedisExpiredMessageListener implements MessageListener {


    @Override
    public void onMessage(Message message, byte[] bytes) {
        // 过期的key
        String expiredKey = new String(message.getBody());


    }

}
