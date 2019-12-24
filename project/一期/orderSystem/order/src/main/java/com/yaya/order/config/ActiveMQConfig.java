package com.yaya.order.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.QosSettings;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Session;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/12/12
 * @description
 */
@Configuration
@Slf4j
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory topicFactory(ConnectionFactory connectionFactory,
                                                           DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        // factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        // factory.setSessionTransacted(true);

        // 持久订阅设置
        // 设置为topic方式目标
        factory.setPubSubDomain(true);

        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory durableTopicFactory(ConnectionFactory durableConnectionFactory,
                                                                  DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, durableConnectionFactory);
        // factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        // factory.setSessionTransacted(true);

        // 持久订阅设置
        // 设置为topic方式目标
        factory.setPubSubDomain(true);
        // 设置客户端id (区分到客户端级别)
        factory.setClientId("client-2");
        // 设置为持久订阅
        factory.setSubscriptionDurable(true);

        return factory;
    }

    @Bean(name = "topicJmsTemplate")
    public JmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory,
                                        MessageConverter jacksonJmsMessageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        // 开启为topic方式
        jmsTemplate.setPubSubDomain(true);
        // 消息的转换方式
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        return jmsTemplate;
    }

    @Bean
    public RedeliveryPolicy redeliveryPolicy(){
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        // 是否在每次失败重发时增长等待时间
        redeliveryPolicy.setUseExponentialBackOff(true);
        // 设置重发最大拖延时间，-1 表示没有拖延，只有setUseExponentialBackOff(true)时生效
        redeliveryPolicy.setMaximumRedeliveryDelay(-1);
        // 重发次数
        redeliveryPolicy.setMaximumRedeliveries(3);
        // 重发时间间隔
        redeliveryPolicy.setInitialRedeliveryDelay(2000);
        // 第一次失败后重发前等待2秒，第二次2*2，依次递增
        redeliveryPolicy.setBackOffMultiplier(2);
        // 是否避免消息碰撞
        redeliveryPolicy.setUseCollisionAvoidance(false);
        // 避免消息碰撞的百分比
        redeliveryPolicy.setCollisionAvoidancePercent((short)5);
        // 开始发送重发消息的延迟时间
        redeliveryPolicy.setRedeliveryDelay(2000);
        // 这2个设置没有作用
        redeliveryPolicy.setQueue("helloQueue1");
        redeliveryPolicy.setTopic("helloTopic1");
        return redeliveryPolicy;
    }

    @Bean(value = "durableConnectionFactory")
    public ConnectionFactory durableConnectionFactory(RedeliveryPolicy redeliveryPolicy) {
        // 创建连接工厂，可以在brokerUrl后面跟上jms.useAsyncSend=true来表示异步发送消息
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        // 设置异步发送消息
        connectionFactory.setAlwaysSyncSend(true);
        // 设置重发的策略
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        // 用于在等待来自节点的确认消息之前，生产者将传输给代理的最大数据字节数。防止超过内存限制
        connectionFactory.setProducerWindowSize(1024000);
        // 针对非持久化消息，用于每次检查是否超过对应的queue或topic内存限制，这个会影响效率
        connectionFactory.setAlwaysSyncSend(true);
        return connectionFactory;
    }

    @Bean
    @Primary
    public JmsTemplate queueJmsTemplate(ConnectionFactory durableConnectionFactory,
                                        MessageConverter jacksonJmsMessageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(durableConnectionFactory);
        // 消息的转换方式
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        // 延迟发送,jms 2.0提供,在activemq中通过在message中设置ScheduledMessage.AMQ_SCHEDULED_PERIOD代替
        //jmsTemplate.setDeliveryDelay(1000);
        // 消息保存机制，是否持久化保存
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        // 是否持久化保存消息
        jmsTemplate.setDeliveryPersistent(true);
        // 发送的消息的优先级，范围是0-9,消费者端优先级范围是0-127，TEST.QUEUE?consumer.priority=10，值越大优先级越高，不起作用
        //jmsTemplate.setPriority(2);
        // 消息的存活时间，如果设置了重试机制，注意存活时间
        jmsTemplate.setTimeToLive(1000);
        // 消息确认机制
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        // 按照名称来设置消息确认机制
        //jmsTemplate.setSessionAcknowledgeModeName("CLIENT_ACKNOWLEDGE");

        // 在没有提供queue或topic情况下，默认提供queue，
        // 需要注意的是使用execute()方法时需要DefaultDestination指定为null，否则报错
        ActiveMQQueue defaultQueue = new ActiveMQQueue("defaultQueue");
        jmsTemplate.setDefaultDestination(defaultQueue);
        // 在没有提供queue或topic情况下，默认提供queue
        jmsTemplate.setDefaultDestinationName("defaultQueue");

        // 使用QosSettings值来提供默认的配置，如果设置了重试机制，注意存活时间
        QosSettings qosSettings = new QosSettings(DeliveryMode.PERSISTENT, 8, 2000);
        jmsTemplate.setQosSettings(qosSettings);
        // 启用QosSettings配置
        jmsTemplate.setExplicitQosEnabled(true);

        // 是否启用消息id，默认true
        jmsTemplate.setMessageIdEnabled(true);
        // 是否启用消息时间，默认true
        jmsTemplate.setMessageTimestampEnabled(true);
        // 设置是否禁止传递由其自己的连接发布的消息，默认false
        jmsTemplate.setPubSubNoLocal(false);
        // 设置消息接收的过期时间
        jmsTemplate.setReceiveTimeout(1000);
        // 设置目标消息的解析类,默认DynamicDestinationResolver
        jmsTemplate.setDestinationResolver(new DynamicDestinationResolver());
        // 设置事务
        jmsTemplate.setSessionTransacted(false);
        return jmsTemplate;
    }
}
