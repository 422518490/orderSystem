package com.yaya.gateway.controller;

import com.yaya.gateway.response.BaseResponse;
import com.yaya.gateway.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/12/25
 * @description hystrix异常的返回调用controller
 */
@RestController
@Slf4j
public class CallbackController {

    /**
     * 根据用户类型获取方法列表断路器产生作用时的回调
     *
     * @return
     */
    @GetMapping(value = "/getMethodNameByTypeHystrix")
    public BaseResponse getMethodNameByTypeHystrix() {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode(ResponseCode.REQUEST_TIME_OUT);
            baseResponse.setMsg("获取方法列表超时");
        } catch (Exception e) {
            baseResponse.setCode(ResponseCode.SERVER_ERROR);
            baseResponse.setMsg("服务器错误");
            log.error("SERVER_ERROR: " + e);
        }
        return baseResponse;
    }
}
