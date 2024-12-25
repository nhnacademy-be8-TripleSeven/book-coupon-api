package com.nhnacademy.bookapi.service.book.impl;


import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCreatorRepository bookCreatorRepository;


    @BeforeEach
    void setUp(){

    }

    @Test
    void test1(){
        bookRepository.deleteAll();
        assertEquals(true, bookRepository.existsById(1L));
    }
}
