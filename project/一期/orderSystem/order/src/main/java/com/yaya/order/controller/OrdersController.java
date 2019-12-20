package com.yaya.order.controller;

import com.yaya.common.constant.RabbitExchangeConstant;
import com.yaya.common.constant.RabbitRoutingKeyConstant;
import com.yaya.common.constant.RedisKeyConstant;
import com.yaya.common.response.BaseResponse;
import com.yaya.common.response.ResponseCode;
import com.yaya.common.util.UUIDUtil;
import com.yaya.order.constant.OrderMQConstant;
import com.yaya.order.constant.UserTypeConstant;
import com.yaya.order.dto.OrderDeleteDTO;
import com.yaya.order.dto.OrdersDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.jms.*;
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

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource(name = "topicJmsTemplate")
    private JmsTemplate topicJmsTemplate;


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

    @DeleteMapping
    public BaseResponse deleteOrder(@RequestParam(value = "orderId") String orderId,
                                    @RequestParam(value = "userType") String userType) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ResponseCode.SUCCESS);
        baseResponse.setMsg("删除订单成功");
        try {
            OrderDeleteDTO orderDeleteDTO = new OrderDeleteDTO();
            orderDeleteDTO.setOrderId(orderId);
            orderDeleteDTO.setUserType(userType);
            jmsTemplate.convertAndSend(OrderMQConstant.DEL_ORDER, orderDeleteDTO);
            // 在jmsTemplate初始化中指定了DefaultDestination，需要在这里置为空，否则报错
            jmsTemplate.setDefaultDestination(null);
            jmsTemplate.execute((session, messageProducer) -> {
                Message message = jmsTemplate.getMessageConverter().toMessage(orderDeleteDTO, session);
                // 添加property属性，因为header为固定的
                message.setStringProperty("delOrder","updateStatus");
                if (UserTypeConstant.MERCHANT.equals(userType)){
                    message.setStringProperty("delUserType","merchant");
                }else {
                    message.setStringProperty("delUserType","other");
                }
                messageProducer.setPriority(5);
                // 同时定义给queue和topic发送
                Queue queue = session.createQueue("queue://helloQueue,topic://durableHelloTopic");
                messageProducer.send(queue,message);
                return null;
            });
        } catch (Exception e) {
            log.error("删除订单错误:{}", e);
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
        }
        return baseResponse;
    }

    @GetMapping("/testMQ")
    public BaseResponse testMQ(){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ResponseCode.SUCCESS);
        baseResponse.setMsg("删除订单成功");
        try {
            OrderDeleteDTO orderDeleteDTO = new OrderDeleteDTO();
            orderDeleteDTO.setOrderId("1");
            orderDeleteDTO.setUserType("01");

            jmsTemplate.convertAndSend("helloQueue1", orderDeleteDTO,message -> {
                message.setStringProperty("queueName","helloQueue1");
                // 延时6秒，间隔2秒 ,投递6次(投递次数=重复次数+默认的一次)
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 6 * 1000L);
                // 投递间隔
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 2 * 1000L);
                // 重复次数
                message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 5);
                // 设置消息的持久化方式
                message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                // 设置消息的延迟发送时间,jms 2.0提供
                //message.setJMSDeliveryTime(1000);
                // 设置消息的发送目标queue
                Destination destination = new ActiveMQQueue("helloQueue");
                message.setJMSDestination(destination);
                // 设置消息过期时间
                message.setJMSExpiration(2000);
                // 消息的优先级，范围是0-9，值越大优先级越高
                //message.setJMSPriority(9);
                // 是否重发消息
                message.setJMSRedelivered(true);
                // 回复消息的queue
                Destination replyDestination = new ActiveMQQueue("replyHelloQueue");
                message.setJMSReplyTo(replyDestination);
                // 发送消息的时间戳，不启用timestamp的时候就是自定义的时间戳
                message.setJMSTimestamp(2576719860905L);
                // 指定消息类型，自定义，消费者消费时可以自定消费的消息
                message.setJMSType("ADD_NEW_MESSAGE");
                return message;
            });

            Thread.sleep(5000);
            jmsTemplate.convertAndSend("helloQueue2", orderDeleteDTO,message -> {
                message.setStringProperty("queueName","helloQueue2");
                //message.setJMSPriority(1);
                return message;
            });

            Thread.sleep(5000);
            topicJmsTemplate.convertAndSend("helloTopic1",orderDeleteDTO,message -> {
                message.setStringProperty("topicName","helloTopic1");
                return message;
            });

            Thread.sleep(5000);
            topicJmsTemplate.convertAndSend("helloTopic2",orderDeleteDTO,message -> {
                message.setStringProperty("topicName","helloTopic2");
                message.setStringProperty("queueName","helloTopic2");
                return message;
            });
        } catch (Exception e) {
            log.error("删除订单错误:{}", e);
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

