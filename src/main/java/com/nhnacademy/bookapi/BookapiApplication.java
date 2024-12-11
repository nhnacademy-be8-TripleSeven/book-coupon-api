package com.nhnacademy.bookapi;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTagRepository;
import com.nhnacademy.bookapi.repository.TagRepository;
import com.nhnacademy.bookapi.service.book_index.BookIndexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class BookapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookapiApplication.class, args);
    }

}
