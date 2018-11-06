package com.yaya.order.service.impl;

import com.yaya.order.dao.OrdersMapper;
import com.yaya.order.dto.OrdersDTO;
import com.yaya.order.service.OrdersService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description
 */
@Service
@RabbitListener(queues = "ordersQueue")
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    @RabbitHandler
    public void addOrders(OrdersDTO ordersDTO) {
        ordersMapper.addOrders(ordersDTO);
        //保存完订单后，发送信息给商家
    }
}
