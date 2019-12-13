package com.yaya.order.constant;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/22
 * @description
 */
public class OrderStatusConstant {
    /**
     * 00---客户取消订单
     */
    public static final String ORDER_CANCEL = "00";

    /**
     * 01---客户生成订单
     */
    public static final String ORDER_NEW = "01";

    /**
     * 02---商家接受订单
     */
    public static final String ORDER_MERCHANT_ACCEPT = "02";

    /**
     * 03---商家拒绝订单
     */
    public static final String ORDER_MERCHANT_REFUSE = "03";

    /**
     * 04----商家完成订单
     */
    public static final String ORDER_MERCHANT_OVER = "04";

    /**
     * 05----送餐员接收订单
     */
    public static final String ORDER_DELIVERY_REVICE = "05";

    /**
     * 06----送餐员配送订单
     */
    public static final String ORDER_DELIVERY_SEND = "06";

    /**
     * 07----送餐员送达订单
     */
    public static final String ORDER_DELIVERY_ARRIVED = "07";

    /**
     * 08----客户收到订单
     */
    public static final String ORDER_CLIENT_REVICE = "08";

    /**
     * 09----送餐员拒绝订单
     */
    public static final String ORDER_DELIVERY_REFUSE = "09";
}
