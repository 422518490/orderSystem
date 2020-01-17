package com.yaya.orderSystem.delivery.service.impl;

import com.yaya.orderSystem.delivery.dao.DeliveryMapperExt;
import com.yaya.orderSystem.delivery.model.Delivery;
import com.yaya.orderSystem.delivery.service.DeliveryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 商家Service接口实现类
 */
@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Resource
    private DeliveryMapperExt deliveryMapperExt;

    @Override
    public void addDelivery(Delivery delivery) {
        deliveryMapperExt.insertSelective(delivery);
    }
}
