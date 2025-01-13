package com.nhnacademy.bookapi.service.book_index;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.exception.BookIndexNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookIndexServiceTest {

    @Mock
    private BookIndexRepository bookIndexRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookIndexService bookIndexService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteIndexSuccess() {
        Long bookId = 1L;
        Book book = new Book();
        BookIndex bookIndex = new BookIndex();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookIndexRepository.findByBook(book)).thenReturn(Optional.of(bookIndex));

        boolean result = bookIndexService.deleteIndex(bookId);

        assertTrue(result);
        verify(bookIndexRepository, times(1)).delete(bookIndex);
    }

    @Test
    void testDeleteIndexBookNotFound() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookIndexService.deleteIndex(bookId));
        verify(bookIndexRepository, never()).delete(any());
    }

    @Test
    void testDeleteIndexBookIndexNotFound() {

        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookIndexRepository.findByBook(book)).thenReturn(Optional.empty());

        assertThrows(BookIndexNotFoundException.class, () -> bookIndexService.deleteIndex(bookId));
        verify(bookIndexRepository, never()).delete(any());
    }

    @Test
    void testGetBookIndexListSuccess() {
        Long bookId = 1L;
        String expectedResult = "Chapter 1, Chapter 2, ....";
        when(bookIndexRepository.findByBookId(bookId)).thenReturn(expectedResult);

        String actualResult = bookIndexService.getBookIndexList(bookId);

        assertEquals(expectedResult, actualResult);
        assertNotNull(actualResult);
        verify(bookIndexRepository, times(1)).findByBookId(bookId);
    }

    @Test
    void testGetBookIndexSuccess() {
        Long bookId = 1L;
        BookIndex bookIndex = new BookIndex();
        when(bookIndexRepository.findIndexByBookId(bookId)).thenReturn(Optional.of(bookIndex));

        BookIndex result = bookIndexService.getBookIndex(bookId);

        assertNotNull(result);
        assertEquals(bookIndex, result);
        verify(bookIndexRepository, times(1)).findIndexByBookId(bookId);
    }

    @Test
    void testGetBookIndexNotFound() {
        Long bookId = 1L;
        when(bookIndexRepository.findIndexByBookId(bookId)).thenReturn(Optional.empty());

        BookIndex result = bookIndexService.getBookIndex(bookId);

        assertNull(result);
        verify(bookIndexRepository, times(1)).findIndexByBookId(bookId);
    }

    @Test
    void testCreateBookIndexSuccess() {
        BookIndex bookIndex = new BookIndex();

        bookIndexService.createBookIndex(bookIndex);

        verify(bookIndexRepository, times(1)).save(bookIndex);
    }

    @Test
    void testDeleteBookIndexSuccess() {
        Long bookId = 1L;

        bookIndexService.deleteBookIndex(bookId);

        verify(bookIndexRepository, times(1)).deleteById(bookId);
    }
}
