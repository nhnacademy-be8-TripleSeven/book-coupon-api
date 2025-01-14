package com.nhnacademy.bookapi.controller.book;

import static org.junit.jupiter.api.Assertions.*;

import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.page.PageDTO;
import com.nhnacademy.bookapi.elasticsearch.dto.DocumentSearchResponseDTO;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import com.nhnacademy.bookapi.elasticsearch.service.BookSearchService;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.service.book.BookService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookSearchControllerTest {

    @InjectMocks
    private BookSearchController bookSearchController;

    @Mock
    private BookService bookService;

    @Mock
    private ElasticSearchBookSearchRepository elasticSearchBookSearchRepository;

    @Mock
    private BookSearchService bookSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookTitleSearchById() {
        // Arrange
        SearchBookDetail mockDetail = new SearchBookDetail("title", "Mock Book", "Mock Author", 2023);
        when(bookService.searchBookDetailByBookId(1L)).thenReturn(mockDetail);

        // Act
        ResponseEntity<SearchBookDetail> response = bookSearchController.bookTitleSearch(1L);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockDetail);
        verify(bookService, times(1)).searchBookDetailByBookId(1L);
    }

    @Test
    void testBookTitleSearchByTerm() {
        // Arrange
        List<DocumentSearchResponseDTO> mockList = Collections.singletonList(new DocumentSearchResponseDTO("1", "Mock Title", "Mock Content"));
        Page<DocumentSearchResponseDTO> mockPage = new PageImpl<>(mockList, PageRequest.of(0, 10), mockList.size());
        when(bookSearchService.elasticSearch(eq("test"), any(PageRequest.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<DocumentSearchResponseDTO>> response = bookSearchController.bookTitleSearch("test", PageRequest.of(0, 10));

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockPage);
        verify(bookSearchService, times(1)).elasticSearch(eq("test"), any(PageRequest.class));
    }

    @Test
    void testBookTypeSearch() {
        // Arrange
        List<BookDetailResponseDTO> mockList = Arrays.asList(
            new BookDetailResponseDTO(1L, "Book1", "Author1", 10000, 10000, "test", LocalDate.now()),
            new BookDetailResponseDTO(2L, "Book2", "Author2", 20000, 10000, "test", LocalDate.now())
        );
        Page<BookDetailResponseDTO> mockPage = new PageImpl<>(mockList, PageRequest.of(0, 10), mockList.size());
        PageDTO<BookDetailResponseDTO> bookDetailResponseDTOPageDTO = new PageDTO<>(
            mockPage.getContent(), mockPage.getNumber(), mockPage.getSize(),
            mockPage.getTotalElements());

        when(bookService.getBookTypeBooks(Type.BOOK, PageRequest.of(0, 10))).thenReturn(bookDetailResponseDTOPageDTO);

        // Act
        ResponseEntity<PageDTO<BookDetailResponseDTO>> response = bookSearchController.bookTypeSearch("BOOK", PageRequest.of(0, 10));


        verify(bookService, times(1)).getBookTypeBooks(Type.BOOK, PageRequest.of(0, 10));
    }



    @Test
    void testGetBookByCategoryId() {
        // Arrange
        List<BookDetailResponseDTO> mockList = Collections.singletonList(
            new BookDetailResponseDTO(1L, "Book1", "Author1", 10000, 10000, "test", LocalDate.now())
        );
        Page<BookDetailResponseDTO> mockPage = new PageImpl<>(mockList, PageRequest.of(0, 10), mockList.size());
        when(bookService.searchBookByCategoryId(1L, PageRequest.of(0, 10))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<BookDetailResponseDTO>> response = bookSearchController.getBookByCategoryId(1L, PageRequest.of(0, 10));

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockPage);
        verify(bookService, times(1)).searchBookByCategoryId(1L, PageRequest.of(0, 10));
    }
}
