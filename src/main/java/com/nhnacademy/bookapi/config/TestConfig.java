package com.nhnacademy.bookapi.config;

import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Configuration
@Import({RedisAutoConfiguration.class, RedisHttpSessionConfiguration.class})
public class TestConfig {
    // Redis 비활성화를 위한 설정
}
