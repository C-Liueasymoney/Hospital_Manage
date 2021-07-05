package com.chong.msm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 11:26 下午
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)  // 取消数据源自动配置
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.chong")
public class MainApplicationMsm {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationMsm.class, args);
    }
}
