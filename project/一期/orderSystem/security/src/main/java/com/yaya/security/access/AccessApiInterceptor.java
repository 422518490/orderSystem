package com.yaya.security.access;


import com.alibaba.fastjson.JSONObject;
import com.yaya.common.constant.PermissionUpdateNoticeConstant;
import com.yaya.common.util.RedisUtil;
import com.yaya.orderapi.CurrentUserData;
import com.yaya.permission.service.PermissionService;
import com.yaya.security.constant.AccessConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 方法权限及令牌验证类
 */
@Component
@PropertySource(ignoreResourceNotFound=true,value= "classpath:config/application-security.properties")
@Slf4j
public class AccessApiInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PermissionService permissionService;


    @Value("${spring.redis.keyTimeout}")
    private long keyTimeout;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AccessRequired annotation = method.getAnnotation(AccessRequired.class);
        try {
            if (annotation != null) {
                String accessToken = request.getHeader("accessToken");
                response.setContentType("text/html;charset=utf-8");
                String methodName = method.getName();
                JSONObject jsonObject = new JSONObject();
                //如果token不为空,则取值权限，为空则返回token为空请求参数错误400
                if (accessToken != null) {
                    // 是否有新增的权限方法
                    boolean updatePermissionNoticeExist = redisUtil.exists(PermissionUpdateNoticeConstant.PERMISSION_UPDATE_NOTICE);
                    CurrentUserData currentUserData = (CurrentUserData) redisUtil.get(accessToken);
                    if(updatePermissionNoticeExist){
                        //根据用户类型获取方法
                        String userType = request.getHeader("userType");
                        List<String> methodNameList = permissionService.getMethodNameByType(userType);
                        currentUserData.setMethodNameList(methodNameList);
                        redisUtil.set(accessToken, currentUserData);
                    }

                    if(Optional.ofNullable(currentUserData).isPresent()){
                        List<String> methodNameList = currentUserData.getMethodNameList();
                        if(Optional.ofNullable(methodNameList).isPresent()
                                && methodNameList.size() > 0){
                            if (methodNameList.contains(methodName)) {
                                redisUtil.updateExpireTime(accessToken, keyTimeout);
                                return true;
                            } else {
                                jsonObject.put("code", HttpStatus.UNAUTHORIZED);
                                jsonObject.put("msg", "该角色没有权限访问或系统未添加此权限.");
                                response.getWriter().print(jsonObject.toString());
                                return false;
                            }
                        }else if(methodNameList.size() == 0){
                            jsonObject.put("code", HttpStatus.UNAUTHORIZED);
                            jsonObject.put("msg", "该角色没有赋予任何权限访问.");
                            response.getWriter().print(jsonObject.toString());
                            return false;
                        }else{
                            jsonObject.put("code", HttpStatus.UNAUTHORIZED);
                            jsonObject.put("msg", "未知的权限错误,请联系管理员.");
                            response.getWriter().print(jsonObject.toString());
                            return false;
                        }
                    }else{
                        jsonObject.put("code", AccessConstant.ACCESS_TOKEN_INVALIDATE);
                        jsonObject.put("msg", "令牌失效,请重新登录.");
                        response.getWriter().print(jsonObject.toString());
                        return false;
                    }
                } else {
                    jsonObject.put("code", AccessConstant.ACCESS_TOKEN_ERROR);
                    jsonObject.put("msg", "未通过鉴权,请求令牌有误.");
                    response.getWriter().print(jsonObject.toString());
                    return false;
                }
            }
        }catch(Exception e){
            log.error("Error from AccessApiInterceptor!",e);
            e.printStackTrace();
        }
        // 没有注解通过拦截
        return true;
    }

}
