package com.yaya.order.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
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

import javax.jms.*;

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

    @Bean(value = "durableConnectionFactory")
    public ConnectionFactory durableConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = null;
        Connection conn;

        try {
            // 创建连接工厂
            connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            // 创建连接
            conn = connectionFactory.createConnection();
            conn.start();


        } catch (JMSException e) {
            log.error("持久连接工厂异常:{}",e);
        }
        return connectionFactory;
    }

    @Bean
    @Primary
    public JmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory,
                                        MessageConverter jacksonJmsMessageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        // 消息的转换方式
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        // 延迟发送,jms 2.0提供
        //jmsTemplate.setDeliveryDelay(1000);
        // 消息保存机制，是否持久化保存
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        // 是否持久化保存消息
        jmsTemplate.setDeliveryPersistent(true);
        // 发送的消息的优先级，范围是0-127，值越大优先级越高
        jmsTemplate.setPriority(2);
        // 消息的存活时间
        jmsTemplate.setTimeToLive(1000);
        // 消息确认机制
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        // 按照名称来设置消息确认机制
        jmsTemplate.setSessionAcknowledgeModeName("CLIENT_ACKNOWLEDGE");

        // 在没有提供queue或topic情况下，默认提供queue，
        // 需要注意的是使用execute()方法时需要DefaultDestination指定为null，否则报错
        ActiveMQQueue defaultQueue = new ActiveMQQueue("defaultQueue");
        jmsTemplate.setDefaultDestination(defaultQueue);
        // 在没有提供queue或topic情况下，默认提供queue
        jmsTemplate.setDefaultDestinationName("defaultQueue");

        // 使用QosSettings值来提供默认的配置
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
