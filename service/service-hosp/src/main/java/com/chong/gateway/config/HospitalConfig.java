package com.chong.gateway.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/20 11:45 下午
 */
//@MapperScan("com.chong.hosp.mapper")
@Configuration
public class HospitalConfig {

//    mybatis-plus分页
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        return paginationInterceptor;
    }
}
