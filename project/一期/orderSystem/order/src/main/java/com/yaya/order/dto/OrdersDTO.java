package com.yaya.order.dto;

import com.yaya.order.model.Orders;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description 订单实体扩展类
 */
@Data
// 自动保存到redis中
@RedisHash(value = "orders")
public class OrdersDTO extends Orders {
}
