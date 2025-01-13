package com.nhnacademy.bookapi.controller.book;

import com.nhnacademy.bookapi.dto.book.*;
import com.nhnacademy.bookapi.service.book.BookMultiTableService;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.book_api.BookApiSaveService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookMultiTableService bookMultiTableService;

    @MockitoBean
    private BookApiSaveService bookApiSaveService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("createBook - 멀티파트 요청으로 책 생성 테스트")
    void createBookMultipartTest() throws Exception {
        // Mock JSON 데이터
        String bookCreatDTOJson = """
        {
            "title": "Test Book",
            "isbn": "1234567890",
            "publishedDate": "2025-01-01",
            "description": "Test Description",
            "regularPrice": 20000,
            "salePrice": 18000,
            "page": 300,
            "stock": 100,
            "index": "Index Content",
            "publisherName": "Test Publisher"
        }
    """;

        // Mock 파일 데이터
        MockMultipartFile coverFile1 = new MockMultipartFile(
            "coverImages", "cover1.jpg", "image/jpeg", "cover-image-1".getBytes());
        MockMultipartFile detailFile1 = new MockMultipartFile(
            "detailImages", "detail1.jpg", "image/jpeg", "detail-image-1".getBytes());

        // Mock JSON 데이터 멀티파트 파일로 설정
        MockMultipartFile bookCreatDTO = new MockMultipartFile(
            "bookCreatDTO", "bookCreatDTO", "application/json", bookCreatDTOJson.getBytes());

        // Mock Service 동작 정의
        doNothing().when(bookMultiTableService).createBook(any(BookCreatDTO.class));

        // 요청 실행 및 검증
        mockMvc.perform(multipart("/admin/books/createBook")
                .file(bookCreatDTO) // JSON 데이터
                .file(coverFile1)   // 파일 데이터
                .file(detailFile1)  // 파일 데이터
                .contentType(MediaType.MULTIPART_FORM_DATA)) // 멀티파트 요청
            .andExpect(status().isCreated()) // 201 응답 기대
            .andDo(print());

        // 서비스 호출 여부 확인
        verify(bookMultiTableService, times(1)).createBook(any(BookCreatDTO.class));
    }
    @Test
    void testAdminBookList() throws Exception {
        when(bookMultiTableService.getAdminBookSearch(anyString(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/admin/books/keyword/test")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(bookMultiTableService, times(1)).getAdminBookSearch(eq("test"), any());
    }

    @Test
    void testUpdateBook() throws Exception {
        // Mock JSON 데이터
        String bookUpdateDTOJson = """
        {
            "id": 1,
            "title": "Updated Title",
            "regularPrice": 22000,
            "salePrice": 20000
        }
    """;

        // Mock 파일 데이터
        MockMultipartFile coverFile1 = new MockMultipartFile(
            "coverImage", "cover1.jpg", "image/jpeg", "updated-cover-image-1".getBytes());
        MockMultipartFile detailFile1 = new MockMultipartFile(
            "detailImage", "detail1.jpg", "image/jpeg", "updated-detail-image-1".getBytes());

        // Mock JSON 데이터 멀티파트 파일로 설정
        MockMultipartFile bookUpdateDTO = new MockMultipartFile(
            "bookUpdateDTO", "bookUpdateDTO", "application/json", bookUpdateDTOJson.getBytes());

        // MockMvc를 이용한 테스트 실행
        mockMvc.perform(multipart("/admin/books/updateBook")
                .file(bookUpdateDTO) // JSON 데이터
                .file(coverFile1)    // 파일 데이터
                .file(detailFile1)   // 파일 데이터
                .contentType(MediaType.MULTIPART_FORM_DATA)) // 멀티파트 요청
            .andExpect(status().isOk()) // 200 응답 기대
            .andDo(print());

        // 서비스 호출 검증
        verify(bookMultiTableService, times(1)).updateBook(any(BookUpdateDTO.class));
    }


    @Test
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/admin/books/delete")
                .param("bookId", "1"))
            .andExpect(status().isNoContent());

        verify(bookMultiTableService, times(1)).deleteBook(eq(1L));
    }

    @Test
    void testGetBookDetail() throws Exception {
        SearchBookDetail detail = new SearchBookDetail();
        when(bookService.searchBookDetailByBookId(1L)).thenReturn(detail);

        mockMvc.perform(get("/books/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(bookService, times(1)).searchBookDetailByBookId(eq(1L));
    }

    @Test
    void testGetBook() throws Exception {
        BookDTO bookDTO = new BookDTO();
        when(bookMultiTableService.getAdminBookById(1L)).thenReturn(bookDTO);

        mockMvc.perform(get("/admin/books/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(bookMultiTableService, times(1)).getAdminBookById(eq(1L));
    }

    @Test
    void testGetBookByIsbn() throws Exception {
        BookApiDTO bookApiDTO = new BookApiDTO();
        when(bookApiSaveService.getAladinBookByIsbn("1234567890")).thenReturn(bookApiDTO);

        mockMvc.perform(get("/admin/books/isbn/1234567890")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(bookApiSaveService, times(1)).getAladinBookByIsbn(eq("1234567890"));
    }

    @Test
    void testGetBookTodayList() throws Exception {
        mockMvc.perform(get("/books/today")
                .cookie(new Cookie("GUEST-ID", "guest123")))
            .andExpect(status().isOk());
    }

    @Test
    void testGetBooksByTag() throws Exception {
        mockMvc.perform(get("/books/tag/scifi"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetBooksByCategory() throws Exception {
        mockMvc.perform(get("/books/booktype/new"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetCartItems() throws Exception {
        when(bookService.getCartItemsByIds(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/books/cartItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
            .andExpect(status().isOk());

        verify(bookService, times(1)).getCartItemsByIds(any());
    }

    @Test
    void testGetBookName() throws Exception {
        when(bookService.getBookName(1L)).thenReturn("Test Book Name");

        mockMvc.perform(get("/books/1/name")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(bookService, times(1)).getBookName(eq(1L));
    }
}
