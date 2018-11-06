package com.yaya.common.util;

import java.util.UUID;

/**
 * @author liaoyubo
 * @version 1.0 2018/2/6
 * @description
 */
public class UUIDUtil {

    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }

}
