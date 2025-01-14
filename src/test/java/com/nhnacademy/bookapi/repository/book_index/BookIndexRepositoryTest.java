package com.nhnacademy.bookapi.repository.book_index;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookIndexRepositoryTest {

    @Mock
    private BookIndexRepository bookIndexRepository;

    private Long bookId;
    private Book book;

    @BeforeEach
    void setUp() {
        bookId = 1L;
        book = new Book();
    }

    @Test
    @DisplayName("findByBook - 도서를 이용한 도서목차 단건 조회 isPresent")
    void findByBookIsPresent() {
        BookIndex bookIndex = new BookIndex();
        when(bookIndexRepository.findByBook(book)).thenReturn(Optional.of(bookIndex));

        Optional<BookIndex> isPresent = bookIndexRepository.findByBook(book);

        assertThat(isPresent).isPresent();
        verify(bookIndexRepository, times(1)).findByBook(book);
    }

    @Test
    @DisplayName("findByBook - 도서를 이용한 도서목차 단건 조회 isEmpty")
    void findByBookIsEmpty() {
        when(bookIndexRepository.findByBook(book)).thenReturn(Optional.empty());
        Optional<BookIndex> isEmpty = bookIndexRepository.findByBook(book);
        assertThat(isEmpty).isEmpty();
        verify(bookIndexRepository, times(1)).findByBook(book);
    }

    @Test
    @DisplayName("findByBookId - 도서 아이디를 이용한 해당 도서목차 목차 조회 isPresent")
    void findByBookIdIsPresent() {
        when(bookIndexRepository.findByBookId(bookId)).thenReturn("1장,2장,3장,...");
        String result = bookIndexRepository.findByBookId(bookId);

        assertThat(result).isEqualTo("1장,2장,3장,...");
        verify(bookIndexRepository, times(1)).findByBookId(bookId);
    }

    @Test
    @DisplayName("findByBookId - 도서 아이디를 이용한 해당 도서목차 목차 조회 isEmpty")
    void findByBookIdIsEmpty() {
        when(bookIndexRepository.findByBookId(bookId)).thenReturn(null);
        String result = bookIndexRepository.findByBookId(bookId);
        assertThat(result).isNull();
        verify(bookIndexRepository, times(1)).findByBookId(bookId);
    }

    @Test
    @DisplayName("findIndexByBookId - 도서 아이디를 이용한 도서목차 컬럼 단건 조회 isPresent")
    void findIndexByBookIdIsPresent() {
        BookIndex bookIndex = new BookIndex();
        when(bookIndexRepository.findIndexByBookId(bookId)).thenReturn(Optional.of(bookIndex));

        Optional<BookIndex> isPresent = bookIndexRepository.findIndexByBookId(bookId);

        assertThat(isPresent).isPresent();
        verify(bookIndexRepository, times(1)).findIndexByBookId(bookId);
    }

    @Test
    @DisplayName("findIndexByBookId - 도서 아이디를 이용한 도서목차 컬럼 단건 조회 isEmpty")
    void findIndexByBookIdIsEmpty() {
        when(bookIndexRepository.findIndexByBookId(bookId)).thenReturn(Optional.empty());
        Optional<BookIndex> isEmpty = bookIndexRepository.findIndexByBookId(bookId);
        assertThat(isEmpty).isEmpty();
        verify(bookIndexRepository, times(1)).findIndexByBookId(bookId);
    }
}
