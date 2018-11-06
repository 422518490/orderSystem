package com.yaya.order.schedule;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author liaoyubo
 * @version 1.0 2018/4/18
 * @description
 */
@Configuration
public class OrderElasticConfig {

    @Resource(name = "regCenter")
    private ZookeeperRegistryCenter zookeeperRegistryCenter;
    @Resource(name = "jobEventRdbConfiguration")
    private JobEventConfiguration jobEventConfiguration;


    @Bean
    public SimpleJob simpleJob() {
        return new OrderElasticJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler productSimpleJobScheduler(@Value("${order.simpleJob.cron}") final String cron,
                                                  @Value("${order.simpleJob.sharingTotalCount}") final int sharingTotalCount,
                                                  @Value("${order.simpleJob.shardingItemParameters}") final String shardingItemParameters) {
        return new SpringJobScheduler(simpleJob(), zookeeperRegistryCenter,
                getLiteJobConfiguration(simpleJob().getClass(), cron, sharingTotalCount, shardingItemParameters),
                jobEventConfiguration);
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(
                new SimpleJobConfiguration(
                        JobCoreConfiguration.newBuilder(
                                jobClass.getName(), cron, shardingTotalCount)
                                .shardingItemParameters(shardingItemParameters)
                                .failover(true)
                                .description("订单定时任务")
                                .build(), jobClass.getCanonicalName()))
                .overwrite(true)
                //设置DUMP作业运行信息监控端口
                .monitorPort(8000)
                .build();
    }

}
