package com.yaya.product.init;

import io.rebloom.client.Client;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/31
 * @description 布隆过滤器初始化
 */
@Component
public class BloomInit {

    @Resource
    private Client bloomClient;

   @PostConstruct
   public void  filterBloom(){
       // 初始化product的bloom过滤大小为1W数据，误判率为10%
       //bloomClient.createFilter("product",10000,0.1);
   }
}
