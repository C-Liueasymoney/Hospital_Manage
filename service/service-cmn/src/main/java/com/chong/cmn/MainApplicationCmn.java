package com.chong.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/26 8:59 下午
 */
@SpringBootApplication
@ComponentScan("com.chong")  // 主要为了扫描其他模块的配置类
public class MainApplicationCmn {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationCmn.class, args);
    }
}
