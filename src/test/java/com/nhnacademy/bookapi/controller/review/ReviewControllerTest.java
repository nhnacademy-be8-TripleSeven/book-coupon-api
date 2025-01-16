package com.nhnacademy.bookapi.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.config.GlobalExceptionHandler;
import com.nhnacademy.bookapi.dto.review.ReviewRequestDto;
import com.nhnacademy.bookapi.dto.review.ReviewResponseDto;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.ReviewAlreadyExistException;
import com.nhnacademy.bookapi.exception.ReviewNotFoundException;
import com.nhnacademy.bookapi.service.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ReviewControllerTest {

    private MockMvc mockMvc;
    private ReviewService reviewService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reviewService = Mockito.mock(ReviewService.class);

        // Controller 인스턴스 생성
        ReviewController reviewController = new ReviewController(reviewService);

        // PageableResolver 등록 (핵심!)
        PageableHandlerMethodArgumentResolver pageableArgumentResolver = new PageableHandlerMethodArgumentResolver();
        pageableArgumentResolver.setFallbackPageable(PageRequest.of(0, 10));

        // standaloneSetup + GlobalExceptionHandler 등록
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /api/reviews - 리뷰 추가")
    class AddReviewTests {

        @Test
        @DisplayName("리뷰 추가 성공 시, 201 Created")
        void testAddReviewSuccess() throws Exception {
            // given
            Long userId = 1L;
            ReviewRequestDto dto = new ReviewRequestDto();
            dto.setBookId(100L);
            dto.setText("리뷰 내용");
            dto.setRating(5);

            // JSON으로 직렬화된 DTO
            MockMultipartFile jsonDto = new MockMultipartFile(
                    "reviewRequestDto",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(dto)
            );

            // MockMultipartFile (이미지 파일)
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test.png",
                    MediaType.IMAGE_PNG_VALUE,
                    "dummy-image".getBytes()
            );

            // service.addReview()가 정상 동작하도록 Mock 설정
            when(reviewService.addReview(eq(userId), any(ReviewRequestDto.class), any())).thenReturn(true);

            // when & then
            mockMvc.perform(multipart("/api/reviews")
                            .file(jsonDto) // JSON 필드 추가
                            .file(mockFile) // 이미지 파일 추가
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE) // Content-Type 명시
                            .header("X-USER", userId)
                    )
                    .andExpect(status().isCreated());
        }


        @Test
        @DisplayName("리뷰 추가 시, BookNotFoundException -> 404 NotFound")
        void testAddReviewBookNotFound() throws Exception {
            // given
            Long userId = 1L;
            ReviewRequestDto dto = new ReviewRequestDto();
            dto.setBookId(999L);
            dto.setText("리뷰 내용");
            dto.setRating(3);

            // JSON으로 직렬화된 DTO
            MockMultipartFile jsonDto = new MockMultipartFile(
                    "reviewRequestDto",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(dto)
            );

            // MockMultipartFile (이미지 파일 없음, 테스트에서 필요시 추가 가능)
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "",
                    MediaType.IMAGE_PNG_VALUE,
                    new byte[0] // 빈 파일로 처리
            );

            // 서비스 계층에서 BookNotFoundException 발생하도록 설정
            when(reviewService.addReview(eq(userId), any(ReviewRequestDto.class), any()))
                    .thenThrow(new BookNotFoundException("Book not found"));

            // when & then
            mockMvc.perform(multipart("/api/reviews")
                            .file(jsonDto) // JSON 데이터
                            .file(mockFile) // 빈 파일 데이터
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .header("X-USER", userId) // 사용자 ID 헤더 추가
                    )
                    .andExpect(status().isNotFound()) // 404 상태 코드 확인
                    .andExpect(jsonPath("$.message").value("Book not found")); // 예외 메시지 확인
        }


        @Test
        @DisplayName("리뷰 추가 시, 이미 리뷰가 존재(ReviewAlreadyExistException) -> 409 Conflict")
        void testAddReviewAlreadyExists() throws Exception {
            // given
            Long userId = 1L;
            ReviewRequestDto dto = new ReviewRequestDto();
            dto.setBookId(100L);
            dto.setText("리뷰 내용");
            dto.setRating(4);

            // JSON으로 직렬화된 DTO
            MockMultipartFile jsonDto = new MockMultipartFile(
                    "reviewRequestDto",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(dto)
            );

            // 이미지 파일 (빈 파일 예제)
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "",
                    MediaType.IMAGE_PNG_VALUE,
                    new byte[0] // 빈 파일 처리
            );

            // 서비스 계층에서 ReviewAlreadyExistException 발생하도록 설정
            when(reviewService.addReview(eq(userId), any(ReviewRequestDto.class), any()))
                    .thenThrow(new ReviewAlreadyExistException("이미 이 책에 리뷰를 작성했습니다."));

            // when & then
            mockMvc.perform(multipart("/api/reviews")
                            .file(jsonDto) // JSON 데이터 파트
                            .file(mockFile) // 이미지 파일 파트
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE) // 멀티파트 요청 명시
                            .header("X-USER", userId) // 사용자 ID 헤더 추가
                    )
                    .andExpect(status().isConflict()) // 409 상태 코드 확인
                    .andExpect(jsonPath("$.message").value("이미 이 책에 리뷰를 작성했습니다.")); // 예외 메시지 확인
        }

    }

    @Nested
    @DisplayName("PUT /api/reviews - 리뷰 수정")
    class UpdateReviewTests {

        @Test
        @DisplayName("리뷰 수정 성공 -> 200 OK")
        void testUpdateReviewSuccess() throws Exception {
            Long userId = 1L;
            ReviewRequestDto dto = new ReviewRequestDto();
            dto.setBookId(100L);
            dto.setText("수정된 내용");
            dto.setRating(4);

            when(reviewService.updateReview(eq(userId), any(ReviewRequestDto.class)))
                    .thenReturn(true);

            mockMvc.perform(put("/api/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User", userId)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("리뷰 수정 시, BookNotFoundException -> 404 NotFound")
        void testUpdateReviewBookNotFound() throws Exception {
            Long userId = 1L;
            ReviewRequestDto dto = new ReviewRequestDto();
            dto.setBookId(999L);
            dto.setRating(3);
            dto.setText("Test review");
            when(reviewService.updateReview(eq(userId), any(ReviewRequestDto.class)))
                    .thenThrow(new BookNotFoundException("Book not found"));

            mockMvc.perform(put("/api/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User", userId)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }

        @Test
        @DisplayName("리뷰 수정 시, ReviewNotFoundException -> 404 NotFound")
        void testUpdateReviewNotFound() throws Exception {
            Long userId = 2L;
            ReviewRequestDto dto = new ReviewRequestDto();
            dto.setBookId(123L);
            dto.setRating(3);
            dto.setText("Test review");
            when(reviewService.updateReview(eq(userId), any(ReviewRequestDto.class)))
                    .thenThrow(new ReviewNotFoundException("Review not found"));

            mockMvc.perform(put("/api/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-User", userId)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Review not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/reviews/all - 특정 유저의 모든 리뷰 조회")
    class GetAllReviewsByUserIdTests {

        @Test
        @DisplayName("유저 리뷰 조회 성공, 데이터 있음 -> 200 OK")
        void testGetAllReviewsSuccess() throws Exception {
            Long userId = 1L;
            List<ReviewResponseDto> reviews = List.of(
                    new ReviewResponseDto(userId, "리뷰1", 5, LocalDateTime.now(), null),
                    new ReviewResponseDto(userId, "리뷰2", 4, LocalDateTime.now(), null)
            );

            when(reviewService.getAllReviewsByUserId(userId)).thenReturn(reviews);

            mockMvc.perform(get("/api/reviews/all")
                            .header("X-User", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].text").value("리뷰1"))
                    .andExpect(jsonPath("$[1].text").value("리뷰2"));
        }

        @Test
        @DisplayName("유저 리뷰 조회 시, 리스트가 비어있으면 -> 404 NotFound")
        void testGetAllReviewsEmpty() throws Exception {
            Long userId = 2L;

            when(reviewService.getAllReviewsByUserId(userId)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/reviews/all")
                            .header("X-User", userId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/reviews/{bookId}/user - 특정 도서에 대한 내 리뷰 조회")
    class GetMyReviewTests {

        @Test
        @DisplayName("내 리뷰 조회 성공 -> 200 OK")
        void testGetMyReviewSuccess() throws Exception {
            Long bookId = 100L;
            Long userId = 1L;
            ReviewResponseDto dto = new ReviewResponseDto(userId, "리뷰 내용", 5, LocalDateTime.now(), null);

            when(reviewService.getReview(bookId, userId)).thenReturn(dto);

            mockMvc.perform(get("/api/reviews/{bookId}/user", bookId)
                            .header("X-User", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.text").value("리뷰 내용"));
        }

        @Test
        @DisplayName("내 리뷰 조회 시, ReviewNotFound -> 404 NotFound")
        void testGetMyReviewNotFound() throws Exception {
            Long bookId = 123L;
            Long userId = 999L;

            when(reviewService.getReview(bookId, userId))
                    .thenThrow(new ReviewNotFoundException("Review not found"));

            mockMvc.perform(get("/api/reviews/{bookId}/user", bookId)
                            .header("X-User", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Review not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/reviews/{bookId}/paged - 특정 도서의 페이징 리뷰 조회")
    class GetPagedReviewsByBookIdTests {

        @Test
        @DisplayName("페이징 리뷰 조회 성공 -> 200 OK")
        void testGetPagedReviewsSuccess() throws Exception {
            Long bookId = 100L;
            List<ReviewResponseDto> content = List.of(
                    new ReviewResponseDto(1L, "리뷰A", 4, LocalDateTime.now(), null),
                    new ReviewResponseDto(2L, "리뷰B", 3, LocalDateTime.now(), null)
            );
            Page<ReviewResponseDto> page = new PageImpl<>(
                    content,
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                    content.size()
            );

            when(reviewService.getPagedReviewsByBookId(eq(bookId), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/reviews/{bookId}/paged", bookId)
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "createdAt,desc")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].text").value("리뷰A"))
                    .andExpect(jsonPath("$.content[1].text").value("리뷰B"));
        }



        @Test
        @DisplayName("페이징 리뷰 조회 시, BookNotFound -> 404 NotFound")
        void testGetPagedReviewsBookNotFound() throws Exception {
            Long bookId = 999L;

            doThrow(new BookNotFoundException("Book not found"))
                    .when(reviewService).getPagedReviewsByBookId(eq(bookId), any(Pageable.class));

            mockMvc.perform(get("/api/reviews/{bookId}/paged", bookId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }
    }

    @Nested
    @DisplayName("GET /api/reviews/{bookId}/all - 특정 도서의 모든 리뷰 조회")
    class GetAllReviewsByBookIdTests {

        @Test
        @DisplayName("도서 전체 리뷰 조회 성공 -> 200 OK")
        void testGetAllReviewsSuccess() throws Exception {
            Long bookId = 100L;
            List<ReviewResponseDto> content = List.of(
                    new ReviewResponseDto(1L, "리뷰1", 3, LocalDateTime.now(), null),
                    new ReviewResponseDto(2L, "리뷰2", 4, LocalDateTime.now(), null)
            );

            when(reviewService.getAllReviewsByBookId(bookId)).thenReturn(content);

            mockMvc.perform(get("/api/reviews/{bookId}/all", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].text").value("리뷰1"));
        }

        @Test
        @DisplayName("도서 전체 리뷰 조회 시, BookNotFound -> 404 NotFound")
        void testGetAllReviewsBookNotFound() throws Exception {
            Long bookId = 999L;
            doThrow(new BookNotFoundException("Book not found"))
                    .when(reviewService).getAllReviewsByBookId(bookId);

            mockMvc.perform(get("/api/reviews/{bookId}/all", bookId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Book not found"));
        }
    }
}
