package com.yaya.common.response;

import lombok.Data;

import java.util.List;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description 集合对象返回
 */
@Data
public class MultiDataResponse<T> extends BaseResponse {

    private List<T> data;
}
