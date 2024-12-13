package com.nhnacademy.bookapi.service.book.impl;


import com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO;
import com.nhnacademy.bookapi.entity.Book;
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
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {
    "spring.elasticsearch.enabled=false"
})
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


    @Test
    void testSaveBook() {
        // Arrange
        Book book = new Book();

        book.setTitle("New Book");
        book.setDescription("A new book description");
        book.setStock(100);

        Mockito.when(bookRepository.save(book)).thenReturn(book);

        // Act
        Book savedBook = bookService.save(book);

        // Assert
        assertNotNull(savedBook);
        assertEquals("New Book", savedBook.getTitle());
        assertEquals("A new book description", savedBook.getDescription());
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    void testUpdateBook_Success() {
        // Arrange
        Book existingBook = new Book();

        existingBook.setTitle("Old Title");

        Book updatedBook = new Book();

        updatedBook.setTitle("Updated Title");

        Mockito.when(bookRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingBook));
        Mockito.when(bookRepository.save(existingBook)).thenReturn(existingBook);

        // Act
        Book result = bookService.update(updatedBook);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(bookRepository, Mockito.times(1)).save(existingBook);
    }

    @Test
    void testUpdateBook_BookNotFound() {
        // Arrange
        Book updatedBook = new Book();

        updatedBook.setTitle("Updated Title");

        Mockito.when(bookRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());


        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
            bookService.update(updatedBook)
        );
        assertEquals("book not found", exception.getMessage());
        Mockito.verify(bookRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(bookRepository, Mockito.never()).save(Mockito.any(Book.class));
    }

    @Test
    void testDeleteBook_Success() {
        // Arrange
        Long bookId = 1L;

        Mockito.doNothing().when(bookRepository).deleteById(bookId);

        // Act
        bookService.delete(bookId);

        // Assert
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(bookId);
    }

    @Test
    void testDeleteBook_BookNotFound() {
        // Arrange
        Long bookId = 1L;

        Mockito.doThrow(new BookNotFoundException("book not found"))
            .when(bookRepository).deleteById(bookId);

        // Act & Assert
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () ->
            bookService.delete(bookId)
        );
        assertEquals("book not found", exception.getMessage());
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(bookId);
    }
}
