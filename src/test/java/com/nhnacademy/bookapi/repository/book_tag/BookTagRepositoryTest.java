package com.nhnacademy.bookapi.repository.book_tag;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.repository.BookTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookTagRepositoryTest {

    @Mock
    private BookTagRepository bookTagRepository;
    private Long bookId;
    private Book book;
    private Tag tag;

    @BeforeEach
    void setUp() {
        bookId = 1L;
        book = new Book();
        book.setTestTitle("Test Title");
        book.setTestId(bookId);

        tag = new Tag();
    }

    @Test
    @DisplayName("existsByBookAndTag - 도서와 태그를 이용한 도서태그 컬럼 여부 확인 exist")
    void existsByBookAndTagExists() {
        when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(true);
        boolean exists = bookTagRepository.existsByBookAndTag(book, tag);
        assertThat(exists).isTrue();
        verify(bookTagRepository, times(1)).existsByBookAndTag(book, tag);
    }

    @Test
    @DisplayName("existsByBookAndTag - 도서와 태그를 이용한 도서태그 컬럼 여부 확인 not exists")
    void exsitsByBookAndTagNotExists() {
        when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(false);
        boolean exists = bookTagRepository.existsByBookAndTag(book, tag);
        assertThat(exists).isFalse();
        verify(bookTagRepository, times(1)).existsByBookAndTag(book, tag);
    }

    @Test
    @DisplayName("findByBookAndTag - 도서와 태그를 이용한 도서태그 컬럼 단건 조회 isPresent")
    void findByBookAndTagPresent() {
        Optional<BookTag> bookTag = Optional.of(new BookTag());
        when(bookTagRepository.findByBookAndTag(book, tag)).thenReturn(bookTag);

        Optional<BookTag> result = bookTagRepository.findByBookAndTag(book, tag);

        assertThat(result).isPresent();
        verify(bookTagRepository, times(1)).findByBookAndTag(book, tag);
    }

    @Test
    @DisplayName("findByBookAndTag - 도서와 태그를 이용한 도서태그 컬럼 단건 조회 isEmpty")
    void findByBookAndTagEmpty() {
        when(bookTagRepository.findByBookAndTag(book, tag)).thenReturn(Optional.empty());
        Optional<BookTag> result = bookTagRepository.findByBookAndTag(book, tag);
        assertThat(result).isEmpty();
        verify(bookTagRepository, times(1)).findByBookAndTag(book, tag);
    }

    @Test
    @DisplayName("findAllByBookWithTags - 도서를 이용한 도서에 달려있는 모든 도서태그 조회 PresentValue")
    void findAllByBookWithTagPresentValue() {
        List<BookTag> bookTags = Arrays.asList(new BookTag(), new BookTag());
        when(bookTagRepository.findAllByBookWithTags(book)).thenReturn(bookTags);
        List<BookTag> result = bookTagRepository.findAllByBookWithTags(book);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst()).isEqualTo(bookTags.getFirst());
        verify(bookTagRepository, times(1)).findAllByBookWithTags(book);
    }

    @Test
    @DisplayName("findAllByBookWithTags - 도서를 이용한 도서에 달려있는 모든 도서태그 조회 empty")
    void findAllByBookWithTagEmpty() {
        when(bookTagRepository.findAllByBookWithTags(book)).thenReturn(Collections.emptyList());
        List<BookTag> result = bookTagRepository.findAllByBookWithTags(book);

        assertThat(result).isEmpty();
        verify(bookTagRepository, times(1)).findAllByBookWithTags(book);
    }

    @Test
    @DisplayName("deleteBookTagByBookId - 도서 아이디를 이용한 해당 도서 아이디의 모든 도서태그 삭제")
    void deleteBookTagByBookId() {
        bookId = 1L;
        doNothing().when(bookTagRepository).deleteBookTagByBookId(bookId);

        bookTagRepository.deleteBookTagByBookId(bookId);
        verify(bookTagRepository, times(1)).deleteBookTagByBookId(bookId);
    }

    @Test
    @DisplayName("existsByBookId - 도서 아이디를 이용한 도서태그 존재 유무 확인 true")
    void existsByBookIdTrue() {
        bookId = 1L;
        when(bookTagRepository.existsByBookId(bookId)).thenReturn(true);

        boolean exists = bookTagRepository.existsByBookId(bookId);
        assertThat(exists).isTrue();
        verify(bookTagRepository, times(1)).existsByBookId(bookId);
    }

    @Test
    @DisplayName("existsByBookId - 도서 아이디를 이용한 도서태그 존재 유무 확인 false")
    void existsByBookIdFalse() {
         bookId = 1L;
        when(bookTagRepository.existsByBookId(bookId)).thenReturn(false);

        boolean exists = bookTagRepository.existsByBookId(bookId);
        assertThat(exists).isFalse();
        verify(bookTagRepository, times(1)).existsByBookId(bookId);
    }

    @Test
    @DisplayName("deleteAllByBook - 도서를 이용한 해당 도서의 도서태그 모두 삭제")
    void deleteAllByBook() {
        doNothing().when(bookTagRepository).deleteAllByBook(book);

        bookTagRepository.deleteAllByBook(book);

        verify(bookTagRepository, times(1)).deleteAllByBook(book);
    }
}
