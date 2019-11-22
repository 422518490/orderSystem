package com.yaya.merchant.config;


import com.yaya.common.util.ExecutorUtil;
import com.yaya.merchant.listerner.RedisExpiredMessageListener;
import com.yaya.merchant.setting.ListenerSetting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/8/3
 * @description redis监听容器配置
 */
@Configuration
public class RedisMessageListenerContainerConfig {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ListenerSetting listenerSetting;

    @Resource
    private RedisExpiredMessageListener redisExpiredMessageListener;

    @Bean
    public RedisMessageListenerContainer messageListenerContainerConfig() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 设置Redis的连接工厂
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        // 设置监听使用的线程池
        container.setTaskExecutor(ExecutorUtil.executorService);
        // 设置监听的Topic
        ChannelTopic channelTopic = new ChannelTopic(listenerSetting.getMerchantTopicExpired());
        // 设置监听器
        container.addMessageListener(redisExpiredMessageListener, channelTopic);

        return container;
    }
}
