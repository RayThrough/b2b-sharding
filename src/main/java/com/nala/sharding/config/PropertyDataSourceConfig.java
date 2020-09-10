package com.nala.sharding.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.nala.sharding.Enum.IdGeneratorTypeEnum;
import com.nala.sharding.config.datasource.DataSource0Config;
import com.nala.sharding.config.datasource.DataSource1Config;
import com.nala.sharding.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件配置数据源
 *
 * Author: 【liansr@nala.com.cn】
 */
@Configuration
@Slf4j
public class PropertyDataSourceConfig {

    @Autowired
    private DataSource0Config dataSource0Config;

    @Autowired
    private DataSource1Config dataSource1Config;

    @Primary
    @Bean("shardingDataSource")
    public DataSource getDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderRuleTableConfiguration());
        shardingRuleConfig.setBindingTableGroups(Collections.singletonList("user"));
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("b2b_0", dataSource0Config.createDataSource());
        dataSourceMap.put("b2b_1", dataSource1Config.createDataSource());
        Properties properties = new Properties();
        properties.setProperty("sql.show", Boolean.TRUE.toString());
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, properties);
    }

    /**
     * 定义分表规则(根据user分表)
     * @return 返回配置信息
     */
    private TableRuleConfiguration getOrderRuleTableConfiguration(){
        TableRuleConfiguration configuration = new TableRuleConfiguration("user", "b2b_${0..1}.user_${0..3}");
        configuration.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", new PreciseModuloShardingDataBaseAlgorithm()));
        configuration.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("type", new PreciseModuloShardingTableAlgorithm()));
        configuration.setKeyGeneratorConfig(getKeyGeneratorConfiguration());
        return configuration;
    }




    /**
     * 定义分布式主键生成方式
     */
    private static KeyGeneratorConfiguration getKeyGeneratorConfiguration(){
        return new KeyGeneratorConfiguration(IdGeneratorTypeEnum.SNOWFLAKE.name(), "id");
    }
}
