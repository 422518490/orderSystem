package com.yaya.security.access;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/7
 * @description 方法权限注解类
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessRequired {

}