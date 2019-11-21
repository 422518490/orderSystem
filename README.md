# OrderingSystem采用spring boot2.0实现
- 1.实现商家的增删改查
- 2.实现产品的增删改查
- 3.实现自定义权限的过滤
- 4.实现eureka及feign的商家和产品服务间调用
- 5.实现日志的查询以及新增
- 6.实现ElasticJob定时器的引入
- 7.实现自定义权限的增删改查
- 8.实现了spring cloud config的github远程配置文件
- 1).由于版本原因，配置webhook后推送到本地不会主动刷新，需要手动调用/actuator/bus-refresh刷新配置缓存
- 9.添加hystrix熔断器
- 10.添加gateway
- 11.新增redis bloom过滤器代码
- 1).需要在Linux上安装Redisbloom，需要redis 4.0以上才支持插件模块，从wget https://github.com/RedisBloom/RedisBloom/archive/v2.0.0.tar.gz
代码后，通过tar -zxvf RedisBloom-2.0.0.tar.gz解压，进入解压文件夹后执行make命令，然后在redis.conf配置文件加上
loadmodule /usr/local/redis-5.0.5/modlues/RedisBloom-2.0.0/redisbloom.so
后重启redis即可。
- 12.merchant实现了优雅关闭程序，需要开启actuator的shutdown端点，通过/actuator/shutdown进行调用
- 13.新增springboot admin模块，完成部分展示功能
- 14.merchant模块新增@RedisCacheManager自动缓存信息
-15.merchant模块新增根据经纬度查询附近商家功能
-16.redis哨兵机制的验证
