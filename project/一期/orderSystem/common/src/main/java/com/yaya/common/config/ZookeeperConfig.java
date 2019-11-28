package com.yaya.common.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/11/28
 * @description
 */
@Configuration
public class ZookeeperConfig {

    @Bean
    public CuratorFramework curatorFramework(@Value("${regCenter.serverList}") final String serverList) {
        // 没有连接上重试10，每次间隔10s
        RetryNTimes retryNTimes = new RetryNTimes(10, 1000);
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.newClient(serverList, retryNTimes);
        curatorFramework.start();
        return curatorFramework;
    }

}
