package com.yaya.merchant.config;

import com.yaya.common.util.ExecutorUtil;
import com.yaya.merchant.listerner.RedisExpiredMessageListener;
import com.yaya.merchant.setting.ListenerSetting;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/11/26
 * @description redis cluster集群key过期监控刷新工厂类
 */
@Component
public class RedisMessageRefreshClusterNodeFactory {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private RedisExpiredMessageListener messageListener;

    @Resource
    private ListenerSetting listenerSetting;

    /**
     * 用于存储已经启动监听的master节点信息
     */
    ConcurrentHashMap masterNodeMap = new ConcurrentHashMap();

    @Scheduled(cron = "0/5 * * * * ?")
    public void refreshClusterNode() {
        RedisClusterConnection clusterConnection = redisConnectionFactory.getClusterConnection();
        if (Optional.ofNullable(clusterConnection).isPresent()) {
            /*
                结束了旧的master节点后，旧的节点为master节点的状态不会改变，
                只有重启旧的master节点后变为了slave节点获取到的状态才会发生变化
            */
            Iterable<RedisClusterNode> redisClusterNodes = clusterConnection.clusterGetNodes();

            // 用于获取当前的master节点信息
            HashMap currentMasterNodeMap = new HashMap();
            redisClusterNodes.forEach(redisClusterNode -> {
                if (redisClusterNode.isMaster()) {
                    String clusterNodeName = "clusterNodeName" + redisClusterNode.hashCode();
                    String hostPort = redisClusterNode.getHost() + ":" + redisClusterNode.getPort();
                    currentMasterNodeMap.put(clusterNodeName, hostPort);
                    // 不进行重复创建监听
                    if (masterNodeMap.containsKey(clusterNodeName)) {
                        return;
                    }
                    masterNodeMap.put(clusterNodeName, hostPort);

                    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisClusterNode.getHost(), redisClusterNode.getPort());
                    LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
                    connectionFactory.afterPropertiesSet();

                    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
                    container.setConnectionFactory(connectionFactory);
                    // 设置监听使用的线程池
                    container.setTaskExecutor(ExecutorUtil.executorService);
                    // 设置监听的Topic
                    ChannelTopic channelTopic = new ChannelTopic(listenerSetting.getMerchantTopicExpired());
                    // 设置监听器
                    container.addMessageListener(messageListener, channelTopic);
                    // 必须调用，否则空指针异常
                    container.afterPropertiesSet();
                    // 启动监控
                    container.start();
                }
            });

            // 比较2个map中的值，去除不存在的值
            masterNodeMap.forEach((key, value) -> {
                if (!currentMasterNodeMap.containsKey(key)) {
                    masterNodeMap.remove(key);
                }
            });

        }
    }

}
