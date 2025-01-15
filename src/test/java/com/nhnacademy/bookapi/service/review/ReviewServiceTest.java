package com.nhnacademy.bookapi.service.review;

import com.nhnacademy.bookapi.dto.review.ReviewRequestDto;
import com.nhnacademy.bookapi.dto.review.ReviewResponseDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.ReviewAlreadyExistException;
import com.nhnacademy.bookapi.exception.ReviewNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.ReviewRepository;
import com.nhnacademy.bookapi.service.object.ObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ObjectService objectService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ReviewService reviewService;

    private Book book;
    private Review review;
    private Long userId;
    private ReviewRequestDto reviewRequestDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        book = new Book();
        book.setTestId(10L); // bookId
        book.setTestTitle("Test Book");

        reviewRequestDto = new ReviewRequestDto();
        reviewRequestDto.setBookId(book.getId());
        reviewRequestDto.setText("Good book!");
        reviewRequestDto.setRating(5);

        review = new Review(
                "Good book!",
                LocalDateTime.now(),
                5,
                book,
                userId,
                "testImageUrl"
        );
    }

    @Nested
    @DisplayName("addReview 메소드 테스트")
    class AddReviewTest {
        @Test
        @DisplayName("성공적으로 리뷰를 추가한다(이미지 파일 존재)")
        void testAddReviewSuccessWithFile() throws IOException {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.existsByBookAndUserId(book, userId)).thenReturn(false);
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("fake image data".getBytes()));

            doNothing().when(objectService).generateAuthToken();
            doNothing().when(objectService).uploadObject(anyString(), anyString(), any());
            when(objectService.getStorageUrl()).thenReturn("https://fake-storage.com");
            // when
            boolean result = reviewService.addReview(userId, reviewRequestDto, multipartFile);
            // then
            assertTrue(result);
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).existsByBookAndUserId(book, userId);
            verify(reviewRepository).save(any(Review.class));
            verify(objectService).generateAuthToken();
            verify(objectService).uploadObject(eq("triple-seven"), contains("review_1_10"), any());
        }

        @Test
        @DisplayName("성공적으로 리뷰를 추가한다(이미지 파일이 null)")
        void testAddReviewSuccessWithNullFile() throws IOException {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.existsByBookAndUserId(book, userId)).thenReturn(false);

            // when
            boolean result = reviewService.addReview(userId, reviewRequestDto, null);

            // then
            assertTrue(result);
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).existsByBookAndUserId(book, userId);
            verify(reviewRepository).save(any(Review.class));
            verify(objectService, never()).generateAuthToken();
            verify(objectService, never()).uploadObject(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("성공적으로 리뷰를 추가한다(이미지 파일이 empty)")
        void testAddReviewSuccessWithEmptyFile() throws IOException {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.existsByBookAndUserId(book, userId)).thenReturn(false);
            when(multipartFile.isEmpty()).thenReturn(true);

            // when
            boolean result = reviewService.addReview(userId, reviewRequestDto, multipartFile);

            // then
            assertTrue(result);
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).existsByBookAndUserId(book, userId);
            verify(reviewRepository).save(any(Review.class));
            verify(objectService, never()).generateAuthToken();
            verify(objectService, never()).uploadObject(anyString(), anyString(), any());
        }


        @Test
        @DisplayName("이미 리뷰가 존재하여 예외 발생")
        void testAddReviewAlreadyExists() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.existsByBookAndUserId(book, userId)).thenReturn(true);

            // when & then
            assertThrows(ReviewAlreadyExistException.class,
                    () -> reviewService.addReview(userId, reviewRequestDto, multipartFile)
            );
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("책이 없어서 예외 발생")
        void testAddReviewBookNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(BookNotFoundException.class,
                    () -> reviewService.addReview(userId, reviewRequestDto, multipartFile)
            );
            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미지 업로드 실패로 인한 RuntimeException 발생")
        void testAddReviewImageUploadFailure() throws IOException {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.existsByBookAndUserId(book, userId)).thenReturn(false);
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getInputStream()).thenThrow(new IOException("Test IOException"));
            doNothing().when(objectService).generateAuthToken();

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> reviewService.addReview(userId, reviewRequestDto, multipartFile)
            );
            assertTrue(ex.getMessage().contains("이미지 업로드 실패"));
            verify(reviewRepository, never()).save(any());
        }


    }

    @Nested
    @DisplayName("updateReview 메소드 테스트")
    class UpdateReviewTest {
        @Test
        @DisplayName("성공적으로 리뷰 수정")
        void testUpdateReviewSuccess() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            // when
            boolean result = reviewService.updateReview(userId, reviewRequestDto);

            // then
            assertTrue(result);
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).findByBookAndUserId(book, userId);
        }

        @Test
        @DisplayName("책이 없어 BookNotFoundException 발생")
        void testUpdateReviewBookNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(BookNotFoundException.class, () ->
                    reviewService.updateReview(userId, reviewRequestDto)
            );
        }

        @Test
        @DisplayName("리뷰가 없어 ReviewNotFoundException 발생")
        void testUpdateReviewNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ReviewNotFoundException.class, () ->
                    reviewService.updateReview(userId, reviewRequestDto)
            );
        }
    }

    @Nested
    @DisplayName("deleteAllReviewsWithBook 메소드 테스트")
    class DeleteAllReviewsWithBookTest {
        @Test
        @DisplayName("해당 도서의 모든 리뷰 삭제 성공")
        void testDeleteAllReviewsWithBook() {
            // given
            Long bookId = book.getId();
            List<Long> userIds = Arrays.asList(1L, 2L, 3L);

            when(reviewRepository.findAllUserIdsByBookId(bookId)).thenReturn(userIds);
            doNothing().when(reviewRepository).deleteByBookId(bookId);
            doNothing().when(objectService).generateAuthToken();
            doNothing().when(objectService).deleteObject(eq("triple-seven"), anyString());

            // when
            reviewService.deleteAllReviewsWithBook(bookId);

            // then
            verify(reviewRepository).findAllUserIdsByBookId(bookId);
            verify(reviewRepository).deleteByBookId(bookId);
            verify(objectService).generateAuthToken();
            // userIds 개수만큼 삭제 호출
            verify(objectService, times(userIds.size())).deleteObject(eq("triple-seven"), anyString());
        }
    }

    @Nested
    @DisplayName("getAllReviewsByUserId 메소드 테스트")
    class GetAllReviewsByUserIdTest {
        @Test
        @DisplayName("유저 ID로 모든 리뷰 조회 성공")
        void testGetAllReviewsByUserId() {
            // given
            List<Review> reviews = Arrays.asList(review, review, review);
            when(reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(reviews);

            // when
            List<ReviewResponseDto> result = reviewService.getAllReviewsByUserId(userId);

            // then
            assertEquals(3, result.size());
            verify(reviewRepository).findAllByUserIdOrderByCreatedAtDesc(userId);
        }
    }

    @Nested
    @DisplayName("getReview 메소드 테스트")
    class GetReviewTest {
        @Test
        @DisplayName("도서에 달려있는 개인 리뷰 조회 성공")
        void testGetReviewSuccess() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            // when
            ReviewResponseDto response = reviewService.getReview(book.getId(), userId);

            // then
            assertNotNull(response);
            assertEquals(userId, response.getUserId());
            assertEquals("Good book!", response.getText());
            verify(reviewRepository).findByBookAndUserId(book, userId);
        }

        @Test
        @DisplayName("책이 없어 BookNotFoundException 발생")
        void testGetReviewBookNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(BookNotFoundException.class, () ->
                    reviewService.getReview(book.getId(), userId)
            );
        }

        @Test
        @DisplayName("리뷰가 없어 ReviewNotFoundException 발생")
        void testGetReviewNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ReviewNotFoundException.class, () ->
                    reviewService.getReview(book.getId(), userId)
            );
        }
    }

    @Nested
    @DisplayName("getPagedReviewsByBookId 메소드 테스트")
    class GetPagedReviewsByBookIdTest {
        @Test
        @DisplayName("페이징 처리된 리뷰 조회 성공")
        void testGetPagedReviewsByBookId() {
            // given
            Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Review> page = new PageImpl<>(Arrays.asList(review, review), pageable, 2);
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findAllByBookOrderByCreatedAtDesc(book, pageable))
                    .thenReturn(page);

            // when
            Page<ReviewResponseDto> result = reviewService.getPagedReviewsByBookId(book.getId(), pageable);

            // then
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            verify(reviewRepository).findAllByBookOrderByCreatedAtDesc(book, pageable);
        }

        @Test
        @DisplayName("책이 없어 BookNotFoundException 발생")
        void testGetPagedReviewsByBookIdBookNotFound() {
            // given
            Pageable pageable = PageRequest.of(0, 2);
            when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(BookNotFoundException.class, () ->
                    reviewService.getPagedReviewsByBookId(book.getId(), pageable)
            );
        }
    }
}
