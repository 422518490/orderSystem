package com.yaya.product.schedule;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liaoyubo
 * @version 1.0 2018/4/18
 * @description
 */
@Slf4j
public class ProductElasticJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("ProductElasticJob定时任务开始。。。。。。");
    }
}
