package com.yaya.order.setting;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/11/28
 * @description
 */
@Data
@Configuration
public class OrderSetting {

    @Value("${order.zookeeper.lockpath}")
    private String zookeeperLock;

    @Value("${order.zookeeper.keeplocktime}")
    private Integer keepLockTime;
}
