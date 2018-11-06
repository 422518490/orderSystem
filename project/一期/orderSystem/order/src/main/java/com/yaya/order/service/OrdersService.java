package com.yaya.order.service;

import com.yaya.order.dto.OrdersDTO;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description 订单service接口类
 */
public interface OrdersService {

    /**
     * 新增订单
     * @param ordersDTO
     */
    void addOrders(OrdersDTO ordersDTO);

}
