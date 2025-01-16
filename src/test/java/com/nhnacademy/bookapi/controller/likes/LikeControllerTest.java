package com.nhnacademy.bookapi.controller.likes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.config.GlobalExceptionHandler;
import com.nhnacademy.bookapi.dto.likes.LikesResponseDto;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.LikeAlreadyExistException;
import com.nhnacademy.bookapi.exception.LikeNotFoundException;
import com.nhnacademy.bookapi.service.likes.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LikeControllerTest {

    private MockMvc mockMvc;
    private LikeService likeService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        likeService = Mockito.mock(LikeService.class);

        LikeController likeController = new LikeController(likeService);

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        pageableResolver.setFallbackPageable(PageRequest.of(0, 10));

        // MockMvc 빌드
        mockMvc = MockMvcBuilders.standaloneSetup(likeController)
                .setCustomArgumentResolvers(pageableResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /api/likes/{bookId} - 좋아요 추가")
    class AddLikeTests {
        @Test
        @DisplayName("좋아요 추가 성공 -> 201 CREATED")
        void testAddLikeSuccess() throws Exception {
            Long userId = 1L;
            Long bookId = 100L;

            doNothing().when(likeService).addLike(userId, bookId);

            mockMvc.perform(post("/api/likes/{bookId}", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("좋아요 추가 시, BookNotFoundException -> 404 NotFound")
        void testAddLikeBookNotFound() throws Exception {
            Long userId = 1L;
            Long bookId = 999L;

            doThrow(new BookNotFoundException("Book not found"))
                    .when(likeService).addLike(userId, bookId);

            mockMvc.perform(post("/api/likes/{bookId}", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }

        @Test
        @DisplayName("좋아요 추가 시, 이미 좋아요가 존재(LikeAlreadyExistException) -> 409 Conflict")
        void testAddLikeAlreadyExist() throws Exception {
            Long userId = 2L;
            Long bookId = 111L;

            doThrow(new LikeAlreadyExistException("The User is already liked"))
                    .when(likeService).addLike(userId, bookId);

            mockMvc.perform(post("/api/likes/{bookId}", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("The User is already liked"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/likes/{bookId} - 좋아요 삭제")
    class DeleteLikeTests {
        @Test
        @DisplayName("좋아요 삭제 성공 -> 204 NO_CONTENT")
        void testDeleteLikeSuccess() throws Exception {
            Long userId = 1L;
            Long bookId = 100L;

            doNothing().when(likeService).deleteLike(userId, bookId);

            mockMvc.perform(delete("/api/likes/{bookId}", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("좋아요 삭제 시, BookNotFoundException -> 404 NotFound")
        void testDeleteLikeBookNotFound() throws Exception {
            Long userId = 1L;
            Long bookId = 999L;

            doThrow(new BookNotFoundException("Book not found"))
                    .when(likeService).deleteLike(userId, bookId);

            mockMvc.perform(delete("/api/likes/{bookId}", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }

        @Test
        @DisplayName("좋아요 삭제 시, LikeNotFoundException -> 404 NotFound")
        void testDeleteLikeNotFound() throws Exception {
            Long userId = 10L;
            Long bookId = 200L;

            doThrow(new LikeNotFoundException("Like not found"))
                    .when(likeService).deleteLike(userId, bookId);

            mockMvc.perform(delete("/api/likes/{bookId}", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Like not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/likes/{bookId}/status - 좋아요 상태 조회")
    class CheckLikeStatusTests {
        @Test
        @DisplayName("좋아요 상태 조회 성공 -> 200 OK & boolean 값 반환")
        void testCheckLikeStatus() throws Exception {
            Long userId = 1L;
            Long bookId = 100L;

            when(likeService.isLiked(userId, bookId)).thenReturn(true);

            mockMvc.perform(get("/api/likes/{bookId}/status", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("좋아요 상태 조회 시, BookNotFoundException -> 404 NotFound")
        void testCheckLikeStatusBookNotFound() throws Exception {
            Long userId = 2L;
            Long bookId = 999L;

            doThrow(new BookNotFoundException("Book not found"))
                    .when(likeService).isLiked(userId, bookId);

            mockMvc.perform(get("/api/likes/{bookId}/status", bookId)
                            .header("X-USER", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/likes/search - 유저의 좋아요 내역 조회 (도서 제목 검색)")
    class SearchLikesByUserAndKeywordTests {
        @Test
        @DisplayName("검색 결과가 있을 경우 -> 200 OK")
        void testSearchLikesSuccess() throws Exception {
            Long userId = 1L;
            String keyword = "keyword";
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

            List<LikesResponseDto> content = List.of(
                    new LikesResponseDto(100L, "도서제목A", LocalDateTime.now()),
                    new LikesResponseDto(101L, "도서제목B", LocalDateTime.now())
            );
            Page<LikesResponseDto> page = new PageImpl<>(content, pageable, content.size());

            when(likeService.getPagedLikesByUserIdAndKeyword(eq(userId), eq(keyword), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/likes/search")
                            .param("keyword", keyword)
                            .param("page", "0")
                            .param("size", "10")
                            .header("X-USER", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].bookId").value(100L))
                    .andExpect(jsonPath("$[1].bookId").value(101L));
        }

        // LikeControllerTest
        @Test
        @DisplayName("검색 결과가 없을 경우 -> 200 OK + 빈 배열")
        void testSearchLikesEmpty() throws Exception {
            Long userId = 1L;
            String keyword = "nothing";

            Page<LikesResponseDto> emptyPage = Page.empty();

            when(likeService.getPagedLikesByUserIdAndKeyword(eq(userId), eq(keyword), any(Pageable.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/likes/search")
                            .param("keyword", keyword)
                            .param("page", "0")
                            .param("size", "10")
                            .header("X-USER", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }


        @Test
        @DisplayName("검색 중 BookNotFoundException 발생 시 -> 404 NotFound")
        void testSearchLikesBookNotFound() throws Exception {
            Long userId = 1L;
            String keyword = "random";

            doThrow(new BookNotFoundException("Book not found"))
                    .when(likeService).getPagedLikesByUserIdAndKeyword(eq(userId), eq(keyword), any(Pageable.class));

            mockMvc.perform(get("/api/likes/search")
                            .param("keyword", keyword)
                            .param("page", "0")
                            .param("size", "10")
                            .header("X-USER", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }
    }
}
