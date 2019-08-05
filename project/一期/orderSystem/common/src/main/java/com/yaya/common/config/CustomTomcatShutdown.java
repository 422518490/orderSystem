package com.yaya.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/8/5
 * @description 自定义关闭Tomcat的springboot应用
 */
@Slf4j
public class CustomTomcatShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

    /**
     * Tomcat 线程池延时关闭的最大等待时间
     */
    private static final int TIME_OUT = 30;

    private volatile Connector connector;


    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    /**
     * 监听Spring 容器关闭的事件，即当前的 ApplicationContext 执行 close 方法
     * @param contextClosedEvent
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        try {
            // 暂停接收外部的所有请求
            this.connector.pause();
            // 获取connector对应的线程
            Executor executor = connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor){
                log.info("准备关闭web应用程序");
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)executor;
                // 准备关闭应用程序，新的请求不能再被处理
                threadPoolExecutor.shutdown();
                // 判断是否处于最大等待时间内
                if (!threadPoolExecutor.awaitTermination(TIME_OUT, TimeUnit.SECONDS)){
                    log.info("web应用等待关闭超过最大时长{},将强制关闭",TIME_OUT);
                    // 如果已经超时线程池还未停止，则强制关闭
                    threadPoolExecutor.shutdownNow();
                    // 判断强制关闭是否失败
                    if (!threadPoolExecutor.awaitTermination(TIME_OUT, TimeUnit.SECONDS)){
                        log.warn("强制关闭应用程序失败");
                    }
                }
            }
        }catch (Exception e){
            log.error("监听关闭web应用程序错误:{}",e);
        }
    }
}
