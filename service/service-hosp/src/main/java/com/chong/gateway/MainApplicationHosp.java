package com.chong.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/20 10:19 下午
 */
//@MapperScan("com.chong.hosp.mapper")
@SpringBootApplication
@ComponentScan(basePackages = "com.chong")
@EnableDiscoveryClient   // nacos服务注册
@EnableFeignClients(basePackages = "com.chong") // 开启服务调用
public class MainApplicationHosp {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationHosp.class, args);
    }
}
