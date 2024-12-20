package com.nhnacademy.bookapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
public class BookapiApplication {
    private static final Logger logger = LoggerFactory.getLogger(BookapiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BookapiApplication.class, args);

        // 테스트 로그 출력
        logger.info("INFO 레벨 로그: 애플리케이션이 시작되었습니다.");
        logger.warn("WARN 레벨 로그: 경고 메시지입니다.");
    }


}
