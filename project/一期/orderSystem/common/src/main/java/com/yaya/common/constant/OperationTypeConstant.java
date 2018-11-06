package com.yaya.common.constant;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/25
 * @description 操作日志类型的常量类
 */
public class OperationTypeConstant {

    /**
     *  商家注册
     */
    public static final String REGISTER_MERCHANT = "01";

    /**
     * 送餐员注册
     */
    public static final String REGISTER_DELIVERY = "02";

    /**
     * 注册普通用户
     */
    public static final String REGISTER_CLIENT = "03";

    /**
     * 用户点餐
     */
    public static final String CLIENT_ORDER = "04";

    /**
     * 用户取消点餐
     */
    public static final String CLIENT_CANCEL_ORDER = "05";

    /**
     * 商家接受订单
     */
    public static final String MERCHANT_ACCEPT_ORDER = "06";

    /**
     * 商家拒绝订单
     */
    public static final String MERCHANT_REFUSE_ORDER = "07";

    /**
     * 送餐员接受订单
     */
    public static final String DELIVERY_ACCEPT_ORDER = "08";

    /**
     * 送餐员送达订单
     */
    public static final String DELIVERY_ARRIVED_ORDER = "09";

    /**
     * 用户确认送达的订单
     */
    public static final String CLIENT_AFFIRM_ARRIVED_ORDER = "10";

    /**
     * 订单扣款成功
     */
    public static final String ORDER_DEDUCT_MONEY_SUCCESS = "11";

    /**
     * 订单扣款失败
     */
    public static final String ORDER_DEDUCT_MONEY_FAILURE = "12";

    /**
     * 商家新增产品
     */
    public static final String MERCHANT_ADD_PRODUCT = "13";

    /**
     * 商家修改产品是否可用
     */
    public static final String MERCHANT_ENABLE_PRODUCT = "14";

    /**
     * 商家恢复产品
     */
    public static final String MERCHANT_RECOVER_PRODUCT = "15";

    /**
     * 商家修改产品的价格
     */
    public static final String MERCHANT_UPDATE_PRODUCT_PRICE = "16";

    /**
     * 商家修改折扣信息
     */
    public static final String MERCHANT_UPDATE_DISCOUNT = "17";

    /**
     * 商家新增送餐员送餐价格
     */
    public static final String MERCHANT_ADD_DELIVERY_PRICE = "18";

    /**
     * 商家修改送餐员送餐价格
     */
    public static final String MERCHANT_UPDATE_DELIVERY_PRICE = "19";

    /**
     * 用户评论商家
     */
    public static final String CLIENT_EVALUATE_MERCHANT = "20";

    /**
     * 用户给商家点赞
     */
    public static final String CLIENT_EVALUATE_COUNT = "21";

    /**
     * 用户付款
     */
    public static final String CLIENT_PAY_MONEY = "22";

    /**
     * 删除商家
     */
    public static final String UPDATE_MERCHANT_ENABLE = "23";

    /**
     * 修改商家信息
     */
    public static final String UPDATE_MERCHANT = "24";

}
