package com.chong.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 8:31 下午
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.chong")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chong")
public class MainApplicationUser {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationUser.class, args);
    }
}
