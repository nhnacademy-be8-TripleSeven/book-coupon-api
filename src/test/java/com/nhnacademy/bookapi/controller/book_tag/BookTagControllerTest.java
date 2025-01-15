package com.nhnacademy.bookapi.controller.book_tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.config.GlobalExceptionHandler;
import com.nhnacademy.bookapi.dto.book_tag.BookTagRequestDTO;
import com.nhnacademy.bookapi.dto.book_tag.BookTagResponseDTO;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.BookTagAlreadyExistException;
import com.nhnacademy.bookapi.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.service.book_tag.BookTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookTagControllerTest {

    private MockMvc mockMvc;
    private BookTagService bookTagService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookTagService = Mockito.mock(BookTagService.class);
        BookTagController controller = new BookTagController(bookTagService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /admin/book-tags - 책-태그 관계 추가")
    class AddBookTagTests {

        @Test
        @DisplayName("성공 시 -> 201 CREATED")
        void testAddBookTagSuccess() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(100L, 200L);

            when(bookTagService.addBookTag(any(BookTagRequestDTO.class))).thenReturn(true);

            mockMvc.perform(post("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());

            verify(bookTagService, times(1)).addBookTag(any(BookTagRequestDTO.class));
        }

        @Test
        @DisplayName("이미 존재하는 책-태그 관계 (BookTagAlreadyExistException) -> 409 Conflict")
        void testAddBookTagAlreadyExist() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(123L, 456L);

            doThrow(new BookTagAlreadyExistException("Book tag already exist"))
                    .when(bookTagService).addBookTag(any(BookTagRequestDTO.class));

            mockMvc.perform(post("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Book tag already exist"));
        }

        @Test
        @DisplayName("책이 존재하지 않음 (BookNotFoundException) -> 404 NotFound")
        void testAddBookTagBookNotFound() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(999L, 777L);

            doThrow(new BookNotFoundException("Book not found"))
                    .when(bookTagService).addBookTag(any(BookTagRequestDTO.class));

            mockMvc.perform(post("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }

        @Test
        @DisplayName("태그가 존재하지 않음 (TagNotFoundException) -> 404 NotFound")
        void testAddBookTagTagNotFound() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(111L, 222L);

            doThrow(new TagNotFoundException("Tag not found"))
                    .when(bookTagService).addBookTag(any(BookTagRequestDTO.class));

            mockMvc.perform(post("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Tag not found"));
        }
    }

    @Nested
    @DisplayName("DELETE /admin/book-tags - 책-태그 관계 삭제")
    class DeleteBookTagTests {
        @Test
        @DisplayName("성공 시 -> 200 OK")
        void testDeleteBookTagSuccess() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(100L, 200L);

            when(bookTagService.deleteBookTag(any(BookTagRequestDTO.class))).thenReturn(true);

            mockMvc.perform(delete("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk());

            verify(bookTagService, times(1)).deleteBookTag(any(BookTagRequestDTO.class));
        }

        @Test
        @DisplayName("삭제 시, 책이 존재하지 않음 (BookNotFoundException) -> 404 NotFound")
        void testDeleteBookTagBookNotFound() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(999L, 888L);

            doThrow(new BookNotFoundException("Book not found"))
                    .when(bookTagService).deleteBookTag(any(BookTagRequestDTO.class));

            mockMvc.perform(delete("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }

        @Test
        @DisplayName("삭제 시, 태그가 존재하지 않음 (TagNotFoundException) -> 404 NotFound")
        void testDeleteBookTagTagNotFound() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(111L, 222L);

            doThrow(new TagNotFoundException("Tag not found"))
                    .when(bookTagService).deleteBookTag(any(BookTagRequestDTO.class));

            mockMvc.perform(delete("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Tag not found"));
        }

        @Test
        @DisplayName("삭제 시, 책-태그 관계 없음 (BookTagNotFoundException) -> 404 NotFound")
        void testDeleteBookTagNotFound() throws Exception {
            BookTagRequestDTO requestDto = new BookTagRequestDTO(300L, 400L);

            doThrow(new BookTagNotFoundException("Not Exist"))
                    .when(bookTagService).deleteBookTag(any(BookTagRequestDTO.class));

            mockMvc.perform(delete("/admin/book-tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Not Exist"));
        }
    }

    @Nested
    @DisplayName("GET /admin/book-tags/{bookId} - 특정 책에 연결된 태그 조회")
    class GetBookTagsByBookTests {
        @Test
        @DisplayName("성공 시 -> 200 OK + 리스트 반환")
        void testGetBookTagsByBookSuccess() throws Exception {
            Long bookId = 100L;
            List<BookTagResponseDTO> responseList = List.of(
                    new BookTagResponseDTO(100L, 1L, "tag1"),
                    new BookTagResponseDTO(100L, 2L, "tag2")
            );

            when(bookTagService.getBookTagsByBook(bookId)).thenReturn(responseList);

            mockMvc.perform(get("/admin/book-tags/{bookId}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].tagName").value("tag1"))
                    .andExpect(jsonPath("$[1].tagName").value("tag2"));
        }

        @Test
        @DisplayName("조회 시, 책이 존재하지 않음 (BookNotFoundException) -> 404 NotFound")
        void testGetBookTagsByBookBookNotFound() throws Exception {
            Long bookId = 999L;

            doThrow(new BookNotFoundException("Book not found"))
                    .when(bookTagService).getBookTagsByBook(bookId);

            mockMvc.perform(get("/admin/book-tags/{bookId}", bookId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }
    }
}
