package com.yaya.common.config;

import org.springframework.context.annotation.Bean;
import org.yeauty.standard.ServerEndpointExporter;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/1/30
 * @description
 */
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
