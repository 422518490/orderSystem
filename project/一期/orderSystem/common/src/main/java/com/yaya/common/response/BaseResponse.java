package com.yaya.common.response;

import lombok.Data;

import java.util.Map;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 返回基础类
 */
@Data
public class BaseResponse {

    //返回码
    private int code;
    //提示信息
    private String msg;

    private Map<String, String> errorMap;

}
