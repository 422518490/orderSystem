package com.yaya.order.controller;

import com.yaya.common.response.BaseResponse;
import com.yaya.common.response.ResponseCode;
import com.yaya.order.dto.OrdersDTO;
import com.yaya.order.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description 订单controller类
 */
@RestController
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AmqpTemplate amqpTemplate;


    public BaseResponse addOrders(@RequestBody OrdersDTO ordersDTO){
        BaseResponse baseResponse = new BaseResponse();
        try {
            Map<String,String> errMap = new HashMap<>();

            if (StringUtils.isEmpty(ordersDTO.getClientId())) {
                errMap.put("clientId","点餐客户ID不能为空");
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("点餐客户ID不能为空");
                return baseResponse;
            }
            if(StringUtils.isEmpty(ordersDTO.getMerchantId())){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("商家ID不能为空");
                return baseResponse;
            }
            if(ordersDTO.getOrderTotalAmount() == null){
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("订单的价格不能为空");
                return baseResponse;
            }
            amqpTemplate.convertAndSend("ordersQueue",ordersDTO);
        }catch (Exception e){
            log.error("新增订单错误:" + e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            e.printStackTrace();
        }
        return baseResponse;
    }

}
