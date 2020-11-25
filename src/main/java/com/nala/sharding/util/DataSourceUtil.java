package com.nala.sharding.util;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * 数据源配置工具类
 * @author liansr@nala.com.cn
 */
public class DataSourceUtil {

    private static final String HOST = "192.168.142.208";
    private static final Integer PORT = 3306;
    private static final String USER_NAME = "root";
    private static final String PASS_WORD = "root";

    public static DataSource createDataSource(final String dataSourceName) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
        druidDataSource.setUrl("jdbc:mysql://"+ HOST +":"+ PORT +"/"+ dataSourceName +"?useAffectedRows=true&userUnicode=true&characterEncoding=utf8&p inGlobalTxToPhysicalConnection=true&serverTimezone=GMT%2b8");
        druidDataSource.setUsername(USER_NAME);
        druidDataSource.setPassword(PASS_WORD);
        return druidDataSource;
    }
}
