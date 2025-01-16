package com.nhnacademy.bookapi.repository.querydsl.Book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookOrderDetailResponse;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Publisher;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(BookRepositoryImpl.class) // Import the custom repository
@Transactional
@ActiveProfiles("test")
class BookRepositoryImplTest {

    @Autowired
    private BookRepositoryImpl bookRepository;

    @Autowired
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(entityManager);

        // Pre-populate data for testing
        Publisher publisher = Publisher.builder().id(1L).name("Test Publisher").build();

        entityManager.persist(publisher);

        Book book1 = Book.builder()
            .title("Test Book 1")
            .isbn13("1234567890123")
            .publishDate(LocalDate.of(2023, 1, 1))
            .description("Description for Book 1")
            .regularPrice(10000)
            .salePrice(9000)
            .stock(50)
            .page(300)
            .publisher(publisher)
            .build();
        entityManager.persist(book1);

        Book book2 = Book.builder()
            .title("Another Book")
            .isbn13("9876543210987")
            .publishDate(LocalDate.of(2022, 5, 15))
            .description("Description for Book 2")
            .regularPrice(12000)
            .salePrice(11000)
            .stock(30)
            .page(250)
            .publisher(publisher)
            .build();
        entityManager.persist(book2);

        entityManager.flush();
    }

    @Test
    void testFindBookById() {
        Long bookId = 1L;
        BookDTO book = bookRepository.findBookById(bookId);

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Test Book 1");
        assertThat(book.getIsbn()).isEqualTo("1234567890123");
    }

    @Test
    void testFindBookByKeyword() {
        String keyword = "Test";
        PageRequest pageable = PageRequest.of(0, 10);

        Page<BookDTO> books = bookRepository.findBookByKeyword(keyword, pageable);

        assertThat(books).isNull();
        assertThat(books.getContent().size()).isEqualTo(1);
        assertThat(books.getContent().get(0).getTitle()).isEqualTo("Test Book 1");
    }

    @Test
    void testFindBookOrderDetail() {
        Long bookId = 1L;
        BookOrderDetailResponse response = bookRepository.findBookOrderDetail(bookId);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Book 1");
        assertThat(response.getRegularPrice()).isEqualTo(10000);
        assertThat(response.getSalePrice()).isEqualTo(9000);
    }
}