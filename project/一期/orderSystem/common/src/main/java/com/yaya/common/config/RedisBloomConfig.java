package com.yaya.common.config;

import io.rebloom.client.Client;
import io.rebloom.client.ClusterClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/7/23
 * @description redis bloom过滤器
 */
@Configuration
@Data
//@ConfigurationProperties(prefix = "spring.redis.cluster")
@ConfigurationProperties(prefix = "spring.redis.sentinel")
public class RedisBloomConfig {

    /*@Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;*/

    private List<String> nodes;

    @Bean
    public Client bloomClient() {
        Client client = new Client("localhost", 6379);
        return client;
    }

    /*@Bean
    public ClusterClient bloomClusterClient() {
        Set<HostAndPort> jedisClusterNodes = new HashSet();
        nodes.forEach(node ->{
            String [] nd = node.split(":");
            jedisClusterNodes.add(new HostAndPort(nd[0],Integer.parseInt(nd[1])));
        });
        ClusterClient clusterClient = new ClusterClient(jedisClusterNodes);
        // 商家登录名的容错率，如果需要更新容错率，只有先删除，或者使用另外的key
        Boolean exists = clusterClient.exists("merchantLoginName");
        if (!exists){
            clusterClient.createFilter("merchantLoginName",10000, 0.0001);
        }
        return clusterClient;
    }*/
}
