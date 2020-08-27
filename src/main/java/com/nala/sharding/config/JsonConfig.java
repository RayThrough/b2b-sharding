package com.nala.sharding.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 前后端交互json转化配置
 * Author:  汪伽 【wangj01@lizi.com】
 * Date:  2019/8/8 9:52
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JsonConfig {

    /**
     * 接口中，自动转换的有：驼峰转换为下划线，空值输出null
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return jacksonObjectMapperBuilder -> {
            //null不返回
            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.ALWAYS);
            jacksonObjectMapperBuilder.failOnUnknownProperties(false);
            //返回给前端转换为下划线模式
//            jacksonObjectMapperBuilder.propertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        };
    }
}
