package com.yaya.order.service.impl;

import com.rabbitmq.client.Channel;
import com.yaya.common.constant.RabbitExchangeConstant;
import com.yaya.common.constant.RabbitRoutingKeyConstant;
import com.yaya.common.constant.RedisKeyConstant;
import com.yaya.common.util.RedisUtil;
import com.yaya.common.util.UUIDUtil;
import com.yaya.order.constant.OrderMQConstant;
import com.yaya.order.constant.OrderStatusConstant;
import com.yaya.order.constant.UserTypeConstant;
import com.yaya.order.dao.OrdersMapperExt;
import com.yaya.order.dto.OrderDeleteDTO;
import com.yaya.order.dto.OrdersDTO;
import com.yaya.order.model.Orders;
import com.yaya.order.model.OrdersExample;
import com.yaya.order.service.OrdersService;
import com.yaya.order.setting.OrderSetting;
import com.yaya.order.socket.OrderSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListeners;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author liaoyubo
 * @version 1.0 2018/3/31
 * @description
 */
@Service
@RabbitListener(queues = "ordersQueue")
@Slf4j
public class OrdersServiceImpl implements OrdersService, RabbitTemplate.ConfirmCallback {

    @Resource
    private OrdersMapperExt ordersMapperExt;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private OrderSocket orderSocket;

    @Resource
    private CuratorFramework curatorFramework;

    @Resource
    private OrderSetting orderSetting;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private JmsTemplate jmsTemplate;

    @Override
    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    public void addOrders(OrdersDTO ordersDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        InterProcessMutex mutexLock = new InterProcessMutex(curatorFramework, orderSetting.getZookeeperLock());
        try {
            // 持有锁的时间为5s
            if (mutexLock.acquire(orderSetting.getKeepLockTime(), TimeUnit.SECONDS)) {
                Thread.sleep(10000);
                ordersDTO.setCreateTime(new Date());
                ordersDTO.setOrderStatus(OrderStatusConstant.ORDER_NEW);
                ordersMapperExt.insertSelective(ordersDTO);

                // 保存完订单后，发送信息给商家
                String uuid = UUIDUtil.getUUID();
                CorrelationData correlationData = new CorrelationData();
                correlationData.setId(uuid);

                //redis中缓存订单信息，防止MQ发送失败
                redisTemplate.opsForHash().put(RedisKeyConstant.ORDER_CREATED_HASH, uuid, ordersDTO);

                rabbitTemplate.convertAndSend(RabbitExchangeConstant.ORDER_EXCHANGE,
                        RabbitRoutingKeyConstant.ORDER_MERCHANT_ROUTING_KEY,
                        ordersDTO,
                        correlationData);

                // 发送一个通知消息给前端(websocket方式)或者APP(采用MQ)说明订单已经创建
                orderSocket.handleMessage("创建订单成功");

                channel.basicAck(tag, false);
            }
        } catch (Exception e) {
            log.error("消费订单信息出错:{}", e);
            try {
                if (e.getMessage().contains("java.sql.SQLIntegrityConstraintViolationException: Duplicate entry")) {
                    channel.basicAck(tag, false);
                }
                String orderFailure = "orderId-" + ordersDTO.getOrderId() + "-failure";
                Object count = redisUtil.get(orderFailure);
                if (!Optional.ofNullable(count).isPresent()) {
                    redisUtil.set(orderFailure, 1, 300L);
                } else {
                    int intCount = Integer.parseInt(count + "");
                    // 超过5次就暂时不创建了
                    if (intCount == 5) {
                        channel.basicAck(tag, false);
                        redisUtil.remove(orderFailure);
                    } else {
                        redisUtil.set(orderFailure, intCount++);
                    }
                }
                // 消费失败，重新发送消息
                channel.basicNack(tag, false, true);
            } catch (IOException ioe) {
                log.error("重新确认消费订单信息错误:{}", ioe);
            }
        } finally {
            try {
                mutexLock.release();
            } catch (Exception e) {
                log.error("释放zookeeper锁失败");
            }
        }
    }

    @JmsListener(destination = OrderMQConstant.DEL_ORDER)
    public void deleteOrder(OrderDeleteDTO orderDeleteDTO) {
        String userType = orderDeleteDTO.getUserType();
        String orderStatus;
        switch (userType) {
            case UserTypeConstant.MERCHANT:
                orderStatus = OrderStatusConstant.ORDER_MERCHANT_REFUSE;
                break;
            case UserTypeConstant.CLIENT:
                orderStatus = OrderStatusConstant.ORDER_CANCEL;
                break;
            case UserTypeConstant.DELIVERY:
                orderStatus = OrderStatusConstant.ORDER_DELIVERY_REFUSE;
                break;
            default:
                orderStatus = "";
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            OrdersExample ordersExample = new OrdersExample();
            OrdersExample.Criteria criteria = ordersExample.createCriteria();
            criteria.andOrderIdEqualTo(orderDeleteDTO.getOrderId());
            Orders orders = new Orders();
            orders.setOrderStatus(orderStatus);
            ordersMapperExt.updateByExampleSelective(orders, ordersExample);
        }
    }

    @JmsListener(destination = "helloQueue", selector = "delOrder='updateStatus'")
    public void jmsQueueTest(OrderDeleteDTO orderDeleteDTO,
                             @Header("delUserType") String delUserType,
                             @Header("delOrder") String delOrder) {
        String userType = orderDeleteDTO.getUserType();
        System.out.println("queue:" + userType + ",delUserType:" + delUserType + ",delOrder:" + delOrder);
    }

    @JmsListener(destination = "helloTopic",
            containerFactory = "topicFactory",
            selector = "delUserType='merchant'")
    public void jmsTopicTest(OrderDeleteDTO orderDeleteDTO,
                             @Header("delUserType") String delUserType,
                             @Header("delOrder") String delOrder) {
        String userType = orderDeleteDTO.getUserType();
        System.out.println("topic:" + userType + ",delUserType:" + delUserType + ",delOrder:" + delOrder);
    }

    /**
     * 同时订阅多个主题
     *
     * @param orderDeleteDTO
     * @param queueName
     */
    @JmsListeners(value = {@JmsListener(destination = "helloQueue1"),
            @JmsListener(destination = "helloQueue2"),
            @JmsListener(destination = "helloTopic2",
                    containerFactory = "topicFactory")})
    public void jmsManyQueueTest(OrderDeleteDTO orderDeleteDTO,
                                 @Header(value = "queueName") String queueName) {
        System.out.println(orderDeleteDTO + ":" + queueName);
    }

    @JmsListeners(value = {@JmsListener(destination = "helloTopic1",
            containerFactory = "topicFactory"),
            @JmsListener(destination = "helloTopic2",
                    containerFactory = "topicFactory")})
    public void jmsManyTopicTest(OrderDeleteDTO orderDeleteDTO,
                                 @Header(value = "topicName") String topicName) {
        System.out.println(orderDeleteDTO + ":" + topicName);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 如果消息投递失败，如何处理
        if (ack) {
            // 删除redis缓存
            redisTemplate.opsForHash().delete(RedisKeyConstant.ORDER_CREATED_HASH, correlationData.getId());
        } else {
            String id = correlationData.getId();
            log.error("uuid为{}的订单已创建发送MQ失败：{}", id, cause);

            // 重新投递
            Object ordersDTO = redisTemplate.opsForHash().get(RedisKeyConstant.ORDER_CREATED_HASH, id);
            if (Optional.ofNullable(ordersDTO).isPresent()) {
                rabbitTemplate.convertAndSend(RabbitExchangeConstant.ORDER_EXCHANGE,
                        RabbitRoutingKeyConstant.ORDER_MERCHANT_ROUTING_KEY,
                        ordersDTO,
                        correlationData);
            }
        }
    }
}
