package com.yaya.common.config;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2020/1/16
 * @description
 */
@Configuration
public class DataSourceConfig {

    @ConfigurationProperties(prefix="spring.datasource.hikari")
    @Bean
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
        //return new MysqlConnectionPoolDataSource();
    }
}
