package com.yaya.order.service;

import com.rabbitmq.client.Channel;
import com.yaya.order.dto.OrdersDTO;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description 订单service接口类
 */
public interface OrdersService {

    /**
     * 新增订单
     *
     * @param ordersDTO
     * @param channel
     * @param tag
     */
    void addOrders(OrdersDTO ordersDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag);

}
