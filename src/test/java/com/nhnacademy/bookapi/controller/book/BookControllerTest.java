package com.nhnacademy.bookapi.controller.book;

import com.nhnacademy.bookapi.book_api.BookApiSaveService;
import com.nhnacademy.bookapi.controller.book.BookController;
import com.nhnacademy.bookapi.dto.book.*;
import com.nhnacademy.bookapi.service.book.BookMultiTableService;
import com.nhnacademy.bookapi.service.book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookControllerTest {

    @InjectMocks
    private BookController bookController;

    @Mock
    private BookService bookService;

    @Mock
    private BookMultiTableService bookMultiTableService;

    @Mock
    private BookApiSaveService bookApiSaveService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBook() throws IOException {
        // Given
        BookCreatDTO bookCreatDTO = new BookCreatDTO();
        List<MultipartFile> coverImages = Collections.emptyList();
        List<MultipartFile> detailImages = Collections.emptyList();

        doNothing().when(bookMultiTableService).createBook(bookCreatDTO);

        // When
        ResponseEntity<Void> response = bookController.createBook(bookCreatDTO, coverImages, detailImages);

        // Then
        assertEquals(201, response.getStatusCodeValue());
        verify(bookMultiTableService, times(1)).createBook(bookCreatDTO);
    }

    @Test
    void testAdminBookList() {
        // Given
        String keyword = "test";
        PageRequest pageable = PageRequest.of(0, 10);
        Page<BookDTO> bookPage = new PageImpl<>(Collections.emptyList());

        when(bookMultiTableService.getAdminBookSearch(keyword, pageable)).thenReturn(bookPage);

        // When
        ResponseEntity<Page<BookDTO>> response = bookController.adminBookList(keyword, pageable);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bookPage, response.getBody());
        verify(bookMultiTableService, times(1)).getAdminBookSearch(keyword, pageable);
    }

    @Test
    void testUpdateBook() throws IOException {
        // Given
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        List<MultipartFile> coverImages = Collections.emptyList();
        List<MultipartFile> detailImages = Collections.emptyList();

        doNothing().when(bookMultiTableService).updateBook(bookUpdateDTO);

        // When
        ResponseEntity<Void> response = bookController.updateBook(bookUpdateDTO, coverImages, detailImages);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(bookMultiTableService, times(1)).updateBook(bookUpdateDTO);
    }

    @Test
    void testDeleteBook() {
        // Given
        Long bookId = 1L;
        doNothing().when(bookMultiTableService).deleteBook(bookId);

        // When
        ResponseEntity<Void> response = bookController.deleteBook(bookId);

        // Then
        assertEquals(204, response.getStatusCodeValue());
        verify(bookMultiTableService, times(1)).deleteBook(bookId);
    }

    @Test
    void testGetBookDetail() {
        // Given
        Long bookId = 1L;
        SearchBookDetail searchBookDetail = new SearchBookDetail();

        when(bookService.searchBookDetailByBookId(bookId)).thenReturn(searchBookDetail);

        // When
        ResponseEntity<SearchBookDetail> response = bookController.getBookDetail(bookId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(searchBookDetail, response.getBody());
        verify(bookService, times(1)).searchBookDetailByBookId(bookId);
    }

    @Test
    void testGetBook() {
        // Given
        Long id = 1L;
        BookDTO bookDTO = new BookDTO();

        when(bookMultiTableService.getAdminBookById(id)).thenReturn(bookDTO);

        // When
        ResponseEntity<BookDTO> response = bookController.getBook(id);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bookDTO, response.getBody());
        verify(bookMultiTableService, times(1)).getAdminBookById(id);
    }

    @Test
    void testGetBookByIsbn() throws Exception {
        // Given
        String isbn = "9781234567890";
        BookApiDTO bookApiDTO = new BookApiDTO();

        when(bookApiSaveService.getAladinBookByIsbn(isbn)).thenReturn(bookApiDTO);

        // When
        ResponseEntity<BookApiDTO> response = bookController.getBookByIsbn(isbn);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bookApiDTO, response.getBody());
        verify(bookApiSaveService, times(1)).getAladinBookByIsbn(isbn);
    }

    @Test
    void testGetBookTodayList() {
        // Given
        String guestId = "test-guest-id";

        // When
        ResponseEntity<Void> response = bookController.getBookTodayList(guestId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetBooksByTag() {
        // Given
        String tag = "fiction";

        // When
        ResponseEntity<Void> response = bookController.getBooksByTag(tag);

        // Then
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetBooksByCategory() {
        // Given
        String bookType = "bestseller";

        // When
        ResponseEntity<Void> response = bookController.getBooksByCategory(bookType);

        // Then
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetCartItems() {
        // Given
        List<Long> bookIds = List.of(1L, 2L, 3L);
        List<CartItemDTO> cartItems = Collections.emptyList();

        when(bookService.getCartItemsByIds(bookIds)).thenReturn(cartItems);

        // When
        ResponseEntity<List<CartItemDTO>> response = bookController.getCartItems(bookIds);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cartItems, response.getBody());
        verify(bookService, times(1)).getCartItemsByIds(bookIds);
    }

    @Test
    void testGetBookName() {
        // Given
        Long bookId = 1L;
        String bookName = "Test Book Name";

        when(bookService.getBookName(bookId)).thenReturn(bookName);

        // When
        ResponseEntity<String> response = bookController.getBookName(bookId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bookName, response.getBody());
        verify(bookService, times(1)).getBookName(bookId);
    }

    @Test
    void testGetBookOrderDetail() {
        // Given
        Long bookId = 1L;
        BookOrderDetailResponse bookOrderDetail = new BookOrderDetailResponse();

        when(bookMultiTableService.getBookOrderDetail(bookId)).thenReturn(bookOrderDetail);

        // When
        ResponseEntity<BookOrderDetailResponse> response = bookController.getBookOrderDetail(bookId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bookOrderDetail, response.getBody());
        verify(bookMultiTableService, times(1)).getBookOrderDetail(bookId);
    }
}
