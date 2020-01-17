package com.yaya.orderSystem.delivery.controller;

import com.yaya.orderSystem.delivery.model.Delivery;
import com.yaya.orderSystem.delivery.service.DeliveryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 送餐员controller类
 */
@RestController
public class DeliveryController {

    @Resource
    private DeliveryService deliveryService;

    @PostMapping(value = "/addDelivery")
    public void addDelivery(@RequestBody Delivery delivery){
        deliveryService.addDelivery(delivery);
    }
}
