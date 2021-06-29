package com.chong.hospital.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/27 8:39 下午
 */
@Configuration
@EnableCaching
public class RedisConfig {

//    配置自定义缓存Key规则,这里可以自己设也可以用简单规则
    @Bean
    public KeyGenerator keyGenerator(){
        return new SimpleKeyGenerator();
    }


    @Bean
    /**
     * 由于@Bean注解可以自动注入参数，所以会将容器中默认的RedisConnectionFactory注入（由lettuce实现）
     */
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);



        return template;
    }

}
