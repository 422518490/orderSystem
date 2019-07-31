package com.yaya.merchant.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2018/10/18
 * @description
 */

@RestController
public class CloudConfigServerController {
    @Value("${test_name}")
    private String name;

    @Value("${test_address}")
    private String address;

    @Value("${test_version}")
    private String version;

    @Value("${test_application_name}")
    private String applicationName;

    @RequestMapping("/test")
    public String test() {
        return "你好，我是" + name + ",年龄：" + address + "地址。当前环境：" + version + ";applicationName是:" + applicationName;
    }

}
