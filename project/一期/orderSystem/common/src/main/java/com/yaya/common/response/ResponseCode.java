package com.yaya.common.response;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/5
 * @description
 */
public class ResponseCode {

    /**
     * 表示返回成功
     */
    public static final int SUCCESS = 200;
    /**
     * 表示API调用时的参数有误
     */
    public static final int PARAMETER_ERROR = 422;
    /**
     * 表示鉴权失败
     */
    public static final int AUTHORIZE_FAILED = 401;
    /**
     * 表示其他服务端错误
     */
    public static final int SERVER_ERROR = 500;

    /**
     * 该用户无权限访问API
     */
    public static final int ACCESS_FAILED=4;

    /**
     * ACCESS_TOKEN失效
     */
    public static final int ACCESS_TOKEN_INVALID=5;

    /**
     * 鉴权未知错误
     */
    public static final int ACCESS_UNKNOWN_ERROR=6;

    /**
     * 未获取ACCESS_TOKEN参数
     */
    public static final int NO_ACCESS_TOKEN=7;

    /**
     * 表示http请求错误
     */
    public static final int HTTP_METHOD_ERROR=415;

    /**
     * 表示资源不存在，被删除或失效
     */
    public static final int SOURCE_NOT_EXIST_ERROR=410;

    /**
     * 请求超时
     */
    public static final int REQUEST_TIME_OUT = 504;

}
