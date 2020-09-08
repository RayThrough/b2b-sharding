package com.nala.sharding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration.class})
@MapperScan("com.nala.sharding.mapper")
public class B2bBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.nala.sharding.B2bBackendApplication.class, args);
    }

}
