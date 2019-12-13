package com.yaya.order.dto;

import lombok.Data;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/12/12
 * @description
 */
@Data
public class OrderDeleteDTO {

    private String orderId;

    /**
     * 用户类型
     */
    private String userType;
}
