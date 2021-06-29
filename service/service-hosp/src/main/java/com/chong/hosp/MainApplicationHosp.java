package com.chong.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/20 10:19 下午
 */
//@MapperScan("com.chong.hosp.mapper")
@SpringBootApplication
@ComponentScan(basePackages = "com.chong")
public class MainApplicationHosp {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationHosp.class, args);
    }
}
