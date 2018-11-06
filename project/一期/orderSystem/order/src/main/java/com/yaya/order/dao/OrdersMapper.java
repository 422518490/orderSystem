package com.yaya.order.dao;

import com.yaya.order.dto.OrdersDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description 订单mapper接口类
 */
@Mapper
public interface OrdersMapper {

    /**
     * 新增订单
     * @param ordersDTO
     */
    void addOrders(OrdersDTO ordersDTO);

}
