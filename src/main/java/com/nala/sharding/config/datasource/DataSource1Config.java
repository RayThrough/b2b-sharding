package com.nala.sharding.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @Date: 2019/11/5 17:22
 * @Author: zhaoyue
 * @Description:
 */
@Data
@Component(value = "sharding_1")
@ConfigurationProperties(prefix = "spring.shardingsphere.datasource.sharding1")
public class DataSource1Config {

    private String url;

    private String username;

    private String password;

    private String type;

    @Value("${spring.shardingsphere.datasource.sharding1.driver-class-name}")
    private String driverClassName;

    public DataSource createDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDbType(type);
        druidDataSource.setDriverClassName(driverClassName);
        return druidDataSource;
    }
}
