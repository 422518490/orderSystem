package com.yaya.common.constant;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/18
 * @description rabbit mq 路由key常量类
 */
public class RabbitRoutingKeyConstant {

    /**
     * 订单队列路由key
     */
    public static final String ORDER_ROUTING_KEY = "topic.order";

    /**
     * 订单商家队列路由key
     */
    public static final String ORDER_MERCHANT_ROUTING_KEY = "topic.order.merchant";
}
