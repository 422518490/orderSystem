package com.yaya.common.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/8/5
 * @description tomcat自定义关闭配置
 */
@Configuration
public class CustomTomcatShutdownConfig {

    @Bean
    public CustomTomcatShutdown customTomcatShutdown(){
        return new CustomTomcatShutdown();
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory(final CustomTomcatShutdown customTomcatShutdown){
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        tomcatServletWebServerFactory.addConnectorCustomizers(customTomcatShutdown);
        return tomcatServletWebServerFactory;
    }
}
