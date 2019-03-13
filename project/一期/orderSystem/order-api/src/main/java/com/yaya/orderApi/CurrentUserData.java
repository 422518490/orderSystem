package com.yaya.orderapi;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/10/13
 * @description 在redis中缓存当前用户信息
 */
@Data
public class CurrentUserData implements Serializable{

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 权限方法集合
     */
    private List<String> methodNameList;
}
