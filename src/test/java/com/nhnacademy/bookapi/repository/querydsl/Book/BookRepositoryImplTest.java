package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookOrderDetailResponse;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Publisher;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookRepositoryImplTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    private Book book1;
    private Publisher publisher;

    @BeforeEach
    void setUp() {
        publisher = publisherRepository.save(new Publisher("Test Publisher"));

        book1 = bookRepository.save(new Book(
                "Test Book Title 1",
                "Description 1",
                LocalDate.of(2023, 1, 1),
                20000,
                15000,
                "9999999999999",
                100,
                300,
                publisher
        ));
    }

    @AfterEach
    void tearDown() {
        // 생성한 데이터만 삭제
        bookRepository.deleteById(book1.getId());
        publisherRepository.deleteById(publisher.getId());
    }

    @Test
    void testFindBookById() {
        // Act
        BookDTO result = bookRepository.findBookById(book1.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(book1.getId());
        assertThat(result.getTitle()).isEqualTo(book1.getTitle());
        assertThat(result.getRegularPrice()).isEqualTo(book1.getRegularPrice());
    }

    @Test
    void testFindBookByKeyword() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<BookDTO> result = bookRepository.findBookByKeyword("Title", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Title");
    }

    @Test
    void testFindBookOrderDetail() {
        // Act
        BookOrderDetailResponse result = bookRepository.findBookOrderDetail(book1.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(book1.getId());
        assertThat(result.getTitle()).isEqualTo(book1.getTitle());
        assertThat(result.getRegularPrice()).isEqualTo(book1.getRegularPrice());
        assertThat(result.getStock()).isEqualTo(book1.getStock());
    }
}
