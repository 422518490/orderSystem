package com.yaya.common.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/26
 * @description 消息队列的基础设置
 */
@Component
@Configuration
public class RabbitMQBasic {

    /**
     *  操作日志消息队列且持久化
     * @return
     */
    @Bean
    public Queue operationLogQueue(){
        return new Queue("operationLogQueue",true);
    }

    /**
     * 订单队列
     * @return
     */
    @Bean
    public Queue ordersQueue(){
        return new Queue("ordersQueue",true);
    }

}
