package com.nala.sharding.config.canal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * canal配置文件
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "canal")
@PropertySource(value = "classpath:/canal/canal-${spring.profiles.active}.properties")
public class CanalConfig {

    /***
     * canal-server host
     */
    private String host;

    /***
     * canal-server port
     */
    private int port;

    /***
     * canal-server userName
     */
    private String userName;

    /***
     * canal-server password
     */
    private String password;

    /**
     * canal-instance
     */
    private String destination;

    /***
     * 监听的数据库
     */
    private List<String> listenerDb;

    /***
     * 监听的表名称列表
     */
    private List<String> listenerTables;
}
