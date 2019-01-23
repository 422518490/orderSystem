package com.yaya.order.service.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.yaya.common.constant.RabbitExchangeConstant;
import com.yaya.common.constant.RabbitRoutingKeyConstant;
import com.yaya.common.util.UUIDUtil;
import com.yaya.order.constant.OrderStatusConstant;
import com.yaya.order.dao.OrdersMapperExt;
import com.yaya.order.dto.OrdersDTO;
import com.yaya.order.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description
 */
@Service
@RabbitListener(queues = "ordersQueue")
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    @Resource
    private OrdersMapperExt ordersMapperExt;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    public void addOrders(OrdersDTO ordersDTO, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        try {
            ordersDTO.setCreateTime(new Date());
            ordersDTO.setOrderStatus(OrderStatusConstant.ORDER_NEW);
            ordersMapperExt.insertSelective(ordersDTO);

            // 保存完订单后，发送信息给商家
            String uuid = UUIDUtil.getUUID();
            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(uuid);
            rabbitTemplate.convertAndSend(RabbitExchangeConstant.ORDER_EXCHANGE,
                    RabbitRoutingKeyConstant.ORDER_ROUTING_KEY,
                    ordersDTO,
                    correlationData);

            // 发送一个通知消息给前端或者APP说明订单已经创建

            channel.basicAck(tag,false);
        } catch (Exception e) {
            log.error("消费订单信息出错:{}",e);
            try {
                // 消费失败，重新发送消息
                channel.basicNack(tag, false, true);
            } catch (IOException ioe) {
                log.error("重新确认消费订单信息错误:{}", ioe);
            }
        }
    }
}
