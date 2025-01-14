package com.nhnacademy.bookapi.controller.book;


import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.page.PageDTO;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BookMainViewControllerTest {

    @InjectMocks
    private BookMainViewController bookMainViewController;

    @Mock
    private BookService bookService;

    @Mock
    private BookCreatorService bookCreatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMonthlyBooks() {
        // Arrange
        List<BookDetailResponseDTO> bookList = Arrays.asList(
            new BookDetailResponseDTO(1L, "Book1", "Author1", 10000, 10000, "test", LocalDate.now()),
            new BookDetailResponseDTO(2L, "Book2", "Author2", 20000, 10000, "test", LocalDate.now())
        );
        Page<BookDetailResponseDTO> mockPage = new PageImpl<>(bookList, PageRequest.of(0, 2), bookList.size());
        PageDTO<BookDetailResponseDTO> bookDetailResponseDTOPageDTO = new PageDTO<>(
            mockPage.getContent(), mockPage.getNumber(), mockPage.getSize(),
            mockPage.getTotalElements());

        when(bookService.getMonthlyBestBooks()).thenReturn(bookDetailResponseDTOPageDTO);

        // Act
        ResponseEntity<List<BookDetailResponseDTO>> response = bookMainViewController.getMonthlyBooks();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(bookList);
        verify(bookService, times(1)).getMonthlyBestBooks();
    }

    @Test
    void testGetRecommendations() {
        // Act
        ResponseEntity<List<BookDetailResponseDTO>> response = bookMainViewController.getRecommendations();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testGetBooksByType() {
        // Arrange
        List<BookDetailResponseDTO> bookList = Arrays.asList(
            new BookDetailResponseDTO(1L, "Book1", "Author1", 10000, 10000, "test", LocalDate.now()),
            new BookDetailResponseDTO(2L, "Book2", "Author2", 20000,10000, "test", LocalDate.now())
        );
        Page<BookDetailResponseDTO> mockPage = new PageImpl<>(bookList, PageRequest.of(0, 15), bookList.size());
        PageDTO<BookDetailResponseDTO> bookDetailResponseDTOPageDTO = new PageDTO<>(
            mockPage.getContent(), mockPage.getNumber(), mockPage.getSize(),
            mockPage.getTotalElements());
        when(bookService.getBookTypeBooks(Type.BOOK, PageRequest.of(0, 15))).thenReturn(bookDetailResponseDTOPageDTO);

        // Act
        ResponseEntity<List<BookDetailResponseDTO>> response = bookMainViewController.getBooksByType("BOOK");

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(bookList);
        verify(bookService, times(1)).getBookTypeBooks(Type.BOOK, PageRequest.of(0, 15));
    }

    @Test
    void testGetBooksByType_InvalidType() {
        // Act & Assert
        assertThatThrownBy(() -> bookMainViewController.getBooksByType("INVALID"))
            .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(bookService);
    }
}
