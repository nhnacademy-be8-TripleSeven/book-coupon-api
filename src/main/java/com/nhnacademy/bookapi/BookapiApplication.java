package com.nhnacademy.bookapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
public class BookapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookapiApplication.class, args);
    }

}
