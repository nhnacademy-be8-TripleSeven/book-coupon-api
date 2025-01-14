package com.nhnacademy.bookapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
//캐싱 활성화
@EnableCaching
public class BookapiApplication {
    private static final Logger logger = LoggerFactory.getLogger(BookapiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BookapiApplication.class, args);

        // 테스트 로그 출력
        logger.info("INFO 레벨 로그: 애플리케이션이 시작되었습니다.");
        logger.warn("WARN 레벨 로그: 경고 메시지입니다.");
    }


}
