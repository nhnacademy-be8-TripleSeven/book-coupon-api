package com.nhnacademy.bookapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication(exclude = {
    ElasticsearchDataAutoConfiguration.class,
    ElasticsearchRestClientAutoConfiguration.class
})
@EnableScheduling
public class BookapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookapiApplication.class, args);
    }

}
