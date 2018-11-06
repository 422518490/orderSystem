package com.yaya.common.response;

import lombok.Data;

import java.util.Map;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 单个对象的返回类
 */
@Data
public class CommonResponse<T> extends BaseResponse {
    /**
     * 单个对象
     */
    private T data;
}
