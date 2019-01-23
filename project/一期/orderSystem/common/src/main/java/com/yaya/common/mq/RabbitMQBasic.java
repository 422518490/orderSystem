package com.yaya.common.mq;

import com.yaya.common.constant.RabbitExchangeConstant;
import com.yaya.common.constant.RabbitQueueConstant;
import com.yaya.common.constant.RabbitRoutingKeyConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
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
     * 操作日志消息队列且持久化
     *
     * @return
     */
    @Bean
    public Queue operationLogQueue() {
        return new Queue("operationLogQueue", true);
    }


    /**
     * 订单交换机
     *
     * @return
     */
    @Bean(name = "ordersExchange")
    public TopicExchange ordersExchange() {
        return new TopicExchange(RabbitExchangeConstant.ORDER_EXCHANGE);
    }

    /**
     * 订单队列,同时持久化
     *
     * @return
     */
    @Bean(name = "ordersQueue")
    public Queue ordersQueue() {
        return new Queue(RabbitQueueConstant.ORDER_QUEUE, true);
    }

    /**
     * 配置订单队列的路由key
     *
     * @param queue
     * @param topicExchange
     * @return
     */
    @Bean
    Binding bindingExchangeOrder(@Qualifier("ordersQueue") Queue queue,
                                 @Qualifier("ordersExchange") TopicExchange topicExchange) {
        // 绑定订单队列到交换机上，同时配置路由key为topic.order
        return BindingBuilder.bind(queue).to(topicExchange).with(RabbitRoutingKeyConstant.ORDER_ROUTING_KEY);
    }

    /**
     * 订单已创建的队列
     * @return
     */
    @Bean(name = "orderCreatedQueue")
    public Queue orderCreatedQueue(){
        return new Queue(RabbitQueueConstant.ORDER_CREATED_QUEUE,true);
    }

    @Bean
    Binding bindingExchangeOrderCreated(@Qualifier("orderCreatedQueue") Queue queue,
                                        @Qualifier("ordersExchange") TopicExchange topicExchange){
        // 绑定订单已创建队列到交换机上，同时配置路由key为topic.order
        return BindingBuilder.bind(queue).to(topicExchange).with(RabbitRoutingKeyConstant.ORDER_ROUTING_KEY);
    }
}
