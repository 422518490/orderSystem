package com.yaya.order.config;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/12/12
 * @description
 */
@Configuration
public class ActiveMQConfig {

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
        // 设置客户端id (区分到客户端级别)
        //factory.setClientId("client-2");
        // 设置为持久订阅
        //factory.setSubscriptionDurable(true);

        return factory;
    }

    @Bean(name = "topicJmsTemplate")
    public JmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory,
                                        MessageConverter jacksonJmsMessageConverter){
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        // 开启为topic方式
        jmsTemplate.setPubSubDomain(true);
        // 消息的转换方式
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        return jmsTemplate;
    }

    @Bean
    @Primary
    public JmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory,
                                        MessageConverter jacksonJmsMessageConverter){
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        // 消息的转换方式
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter);
        return jmsTemplate;
    }
}
