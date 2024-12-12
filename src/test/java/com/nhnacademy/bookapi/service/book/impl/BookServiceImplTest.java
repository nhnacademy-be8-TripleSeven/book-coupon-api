package com.nhnacademy.bookapi.service.book.impl;


import com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCreatorRepository bookCreatorRepository;

    @Test
    void testSearchBookDetailByBookId_Success() {
        // Arrange
        Long bookId = 1L;

        SearchBookDetailDTO searchBookDetailDTO = new SearchBookDetailDTO(
            "Title", "Description", LocalDate.of(2024, 12, 12),
            10000, 9000, "1234567890123", 100, 200, "coverUrl", "Publisher"
        );

        BookCreator bookCreator = new BookCreator();

        bookCreator.setName("Author Name");

        Mockito.when(bookRepository.searchBookById(bookId)).thenReturn(Optional.of(searchBookDetailDTO));
        Mockito.when(bookCreatorRepository.findCreatorByBookId(bookId)).thenReturn(List.of(bookCreator));

        // Act
        SearchBookDetailDTO result = bookService.searchBookDetailByBookId(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(searchBookDetailDTO, result);
        assertEquals(1, result.getBookCreators().size());
        assertEquals("Author Name", result.getBookCreators().get(0).getName());

        Mockito.verify(bookRepository, Mockito.times(1)).searchBookById(bookId);
        Mockito.verify(bookCreatorRepository, Mockito.times(1)).findCreatorByBookId(bookId);
    }

    @Test
    void testSearchBookDetailByBookId_BookNotFound() {
        // Arrange
        Long bookId = 1L;

        Mockito.when(bookRepository.searchBookById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
            bookService.searchBookDetailByBookId(bookId)
        );
        assertEquals("Book not found", exception.getMessage());

        Mockito.verify(bookRepository, Mockito.times(1)).searchBookById(bookId);
        Mockito.verify(bookCreatorRepository, Mockito.never()).findCreatorByBookId(Mockito.anyLong());
    }

    @Test
    void testSearchBookDetailByBookId_BookCreatorNotFound() {
        // Arrange
        Long bookId = 1L;

        SearchBookDetailDTO searchBookDetailDTO = new SearchBookDetailDTO(
            "Title", "Description", LocalDate.of(2024, 12, 12),
            10000, 9000, "1234567890123", 100, 200, "coverUrl", "Publisher"
        );

        Mockito.when(bookRepository.searchBookById(bookId)).thenReturn(Optional.of(searchBookDetailDTO));
        Mockito.when(bookCreatorRepository.findCreatorByBookId(bookId)).thenReturn(Collections.emptyList());

        // Act & Assert
        BookCreatorNotFoundException exception = assertThrows(BookCreatorNotFoundException.class, () ->
            bookService.searchBookDetailByBookId(bookId)
        );
        assertEquals("BookCreator not found", exception.getMessage());

        Mockito.verify(bookRepository, Mockito.times(1)).searchBookById(bookId);
        Mockito.verify(bookCreatorRepository, Mockito.times(1)).findCreatorByBookId(bookId);
    }
}
