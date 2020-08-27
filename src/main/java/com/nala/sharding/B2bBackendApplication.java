package com.nala.sharding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nala.sharding.mapper")
public class B2bBackendApplication {

    public static void main(String[] args) {
        System.out.println("启动成功");
        SpringApplication.run(com.nala.sharding.B2bBackendApplication.class, args);
    }

}
