//package com.nala.sharding.config;
//
//import com.nala.sharding.Enum.IdGeneratorTypeEnum;
//import com.nala.sharding.util.DataSourceUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
//import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
//import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
//import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.*;
//
///**
// * 需要自己定义数据源
// * Author: 【liansr@nala.com.cn】
// */
//@Configuration
//@Slf4j
//public class DataSourceConfig {
//
////    /**
////     * SqlSessionFactory 实体
////     */
////    @Bean
////    public SqlSessionFactory sqlSessionFactory() throws Exception {
////        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
////        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
////        sessionFactory.setDataSource(buildDataSource());
////        sessionFactory.setFailFast(true);
////        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/**/*.xml"));
////        sessionFactory.setTypeAliasesPackage("com.nala.sharding.domain");
////        return sessionFactory.getObject();
////    }
//
//    @Bean
//    public DataSource getDataSource() throws SQLException {
//        return buildDataSource();
//    }
//
//    private DataSource buildDataSource() throws SQLException {
//        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
//        shardingRuleConfig.getTableRuleConfigs().add(getOrderRuleTableConfiguration());
//        shardingRuleConfig.setBindingTableGroups(Collections.singletonList("user"));
////        shardingRuleConfig.getBroadcastTables().add("t_config");
//        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new Properties());
//    }
//
//    /**
//     * 定义分表规则(根据user分表)
//     * @return 返回配置信息
//     */
//    private TableRuleConfiguration getOrderRuleTableConfiguration(){
//        TableRuleConfiguration configuration = new TableRuleConfiguration("user", "b2b_${1..2}.user_${0..3}");
//        configuration.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("id", "b2b_${id % 3}"));
//        configuration.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("type", new PreciseModuloShardingTableAlgorithm()));
////        configuration.setKeyGeneratorConfig(getKeyGeneratorConfiguration());
//        return configuration;
//    }
//
//    /**
//     * 创建数据库
//     */
//    private Map<String, DataSource> createDataSourceMap() {
//        Map<String, DataSource> result = new HashMap<>();
//        result.put("b2b_0", DataSourceUtil.createDataSource("b2b_0"));
//        result.put("b2b_1", DataSourceUtil.createDataSource("b2b_1"));
//        return result;
//    }
//
////    /**
////     * 定义分布式主键生成方式
////     */
////    private static KeyGeneratorConfiguration getKeyGeneratorConfiguration(){
////        return new KeyGeneratorConfiguration(IdGeneratorTypeEnum.SNOWFLAKE.name(), "id");
////    }
//}
