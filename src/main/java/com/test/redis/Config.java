package com.test.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.List;

// @Configuration
class Config {
    List<String> clusterNodes = Arrays.asList("47.114.128.1:6379", "47.114.128.1:6379");

    @Bean
    RedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(new RedisClusterConfiguration(clusterNodes));
    }

    @Bean
    RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {

        // just used StringRedisTemplate for simplicity here.
        return new StringRedisTemplate(factory);
    }
}