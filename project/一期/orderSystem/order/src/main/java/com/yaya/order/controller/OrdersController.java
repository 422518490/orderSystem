package com.yaya.order.controller;

import com.yaya.common.constant.RabbitExchangeConstant;
import com.yaya.common.constant.RabbitRoutingKeyConstant;
import com.yaya.common.constant.RedisKeyConstant;
import com.yaya.common.response.BaseResponse;
import com.yaya.common.response.ResponseCode;
import com.yaya.common.util.UUIDUtil;
import com.yaya.order.dto.OrdersDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description 订单controller类
 */
@RestController
@Slf4j
@RequestMapping(value = "/order")
public class OrdersController implements ConfirmCallback {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate redisTemplate;


    @PostMapping
    public BaseResponse addOrders(@RequestBody OrdersDTO ordersDTO) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ResponseCode.SUCCESS);
        baseResponse.setMsg("新增订单成功");
        try {
            Map<String, String> errMap = new HashMap<>();

            if (StringUtils.isEmpty(ordersDTO.getClientId())) {
                errMap.put("clientId", "点餐客户ID不能为空");
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("点餐客户ID不能为空");
                return baseResponse;
            }
            if (StringUtils.isEmpty(ordersDTO.getMerchantId())) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("商家ID不能为空");
                return baseResponse;
            }
            if (ordersDTO.getOrderTotalAmount() == null) {
                baseResponse.setCode(ResponseCode.PARAMETER_ERROR);
                baseResponse.setMsg("订单的价格不能为空");
                return baseResponse;
            }

            redisTemplate.opsForValue().set("orderTest", ordersDTO, 10, TimeUnit.SECONDS);

            log.info("orderTest:{}", redisTemplate.opsForValue().get("orderTest"));

            //amqpTemplate.convertAndSend("ordersQueue",ordersDTO);
            // 用于保证传送到rabbit mq后没有正确保存时的回调执行判断
            // 生成uuid主键
            String uuid = UUIDUtil.getUUID();
            ordersDTO.setOrderId(uuid);

            //redis中缓存订单信息，防止MQ发送失败
            redisTemplate.opsForHash().put(RedisKeyConstant.ORDER_HASH, uuid, ordersDTO);

            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(uuid);
            rabbitTemplate.convertAndSend(RabbitExchangeConstant.ORDER_EXCHANGE,
                    RabbitRoutingKeyConstant.ORDER_ROUTING_KEY,
                    ordersDTO,
                    correlationData);
        } catch (Exception e) {
            log.error("新增订单错误:{}", e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
        }
        return baseResponse;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 如果消息投递失败，如何处理
        if (ack) {
            // 删除redis缓存
            redisTemplate.opsForHash().delete(RedisKeyConstant.ORDER_HASH, correlationData.getId());
        } else {
            String id = correlationData.getId();
            log.error("uuid为{}的订单发送MQ失败：{}", id, cause);

            // 重新投递
            Object ordersDTO = redisTemplate.opsForHash().get(RedisKeyConstant.ORDER_HASH, id);
            if (Optional.ofNullable(ordersDTO).isPresent()) {
                rabbitTemplate.convertAndSend(RabbitExchangeConstant.ORDER_EXCHANGE,
                        RabbitRoutingKeyConstant.ORDER_ROUTING_KEY,
                        (OrdersDTO) ordersDTO,
                        correlationData);
            }
        }
    }
}
