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
import java.io.InputStream;
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
        @DisplayName("책이 없어서 BookNotFoundException 발생")
        void testUpdateReview_BookNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(BookNotFoundException.class,
                    () -> reviewService.updateReview(userId, reviewRequestDto, multipartFile, true)
            );

            verify(bookRepository).findById(book.getId());
            verifyNoMoreInteractions(bookRepository, reviewRepository, objectService);
        }

        @Test
        @DisplayName("리뷰가 없어서 ReviewNotFoundException 발생")
        void testUpdateReview_ReviewNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.updateReview(userId, reviewRequestDto, multipartFile, true)
            );

            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).findByBookAndUserId(book, userId);
            verifyNoMoreInteractions(reviewRepository, objectService);
        }

        @Test
        @DisplayName("isRemoveImage=true && 파일 존재 (새 이미지 업로드) - 기존 이미지 삭제 후 업로드 시나리오")
        void testUpdateReview_RemoveImage_True_WithNewFile() throws IOException {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            // file mock
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("fake data".getBytes()));

            doNothing().when(objectService).generateAuthToken();
            // 업로드 과정을 검증하기 위해 doNothing 설정
            doNothing().when(objectService).uploadObject(eq("triple-seven"), anyString(), any(InputStream.class));
            // 이미지는 삭제되지 않고 "새 업로드"만 될 것이므로 deleteObject는 호출되지 않음

            // when
            boolean result = reviewService.updateReview(userId, reviewRequestDto, multipartFile, true);

            // then
            assertTrue(result);
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).findByBookAndUserId(book, userId);

            // generateAuthToken 호출
            verify(objectService).generateAuthToken();
            // 새로운 파일 업로드
            verify(objectService).uploadObject(eq("triple-seven"), contains("review_" + userId + "_" + book.getId()), any(InputStream.class));
            // 기존 이미지 삭제는 호출되지 않음 (여기 로직상 '기존 이미지 삭제' → '새 파일 업로드'가 아닌,
            // "기존 오브젝트"를 overwrite한다고 가정. 아래 줄 참고)
            verify(objectService, never()).deleteObject(eq("triple-seven"), anyString());

            // 리뷰 엔티티 수정 여부 확인
            assertEquals(reviewRequestDto.getText(), review.getText());
            assertEquals(reviewRequestDto.getRating(), review.getRating());
            assertNotNull(review.getImageUrl());
        }

        @Test
        @DisplayName("isRemoveImage=true && 파일이 null or empty (기존 이미지 삭제만)")
        void testUpdateReview_RemoveImage_True_NoFile() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            doNothing().when(objectService).generateAuthToken();
            doNothing().when(objectService).deleteObject(eq("triple-seven"), anyString());

            // multipartFile이 null이거나 empty이므로 업로드 로직은 실행되지 않음
            // --> 여기서는 null로 가정
            // (empty 파일인 경우에도 로직 동일하게 delete만 실행)
            MultipartFile emptyFile = null;

            // when
            boolean result = reviewService.updateReview(userId, reviewRequestDto, emptyFile, true);

            // then
            assertTrue(result);
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).findByBookAndUserId(book, userId);

            verify(objectService).generateAuthToken();
            // 기존 이미지 삭제만
            verify(objectService).deleteObject(eq("triple-seven"), contains("review_" + userId + "_" + book.getId()));
            // 업로드는 호출되지 않음
            verify(objectService, never()).uploadObject(anyString(), anyString(), any(InputStream.class));

            // 리뷰 엔티티 수정 여부
            assertEquals(reviewRequestDto.getText(), review.getText());
            assertEquals(reviewRequestDto.getRating(), review.getRating());
            // imageUrl가 null로 업데이트되었는지 확인 가능 (review.updateImageUrl(imageUrl))
            assertNull(review.getImageUrl());
        }

        @Test
        @DisplayName("isRemoveImage=false && 새 파일 존재 (새 이미지 업로드 - 기존 이미지는 없거나 덮어씀)")
        void testUpdateReview_RemoveImage_False_WithNewFile() throws IOException {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("fake data".getBytes()));

            doNothing().when(objectService).generateAuthToken();
            doNothing().when(objectService).uploadObject(eq("triple-seven"), anyString(), any(InputStream.class));

            // when
            boolean result = reviewService.updateReview(userId, reviewRequestDto, multipartFile, false);

            // then
            assertTrue(result);
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).findByBookAndUserId(book, userId);

            verify(objectService).generateAuthToken();
            verify(objectService).uploadObject(eq("triple-seven"), contains("review_" + userId + "_" + book.getId()), any(InputStream.class));
            // 기존 이미지 삭제는 호출되지 않음
            verify(objectService, never()).deleteObject(eq("triple-seven"), anyString());

            assertEquals(reviewRequestDto.getText(), review.getText());
            assertEquals(reviewRequestDto.getRating(), review.getRating());
            assertNotNull(review.getImageUrl());
        }

        @Test
        @DisplayName("isRemoveImage=false && 파일이 null -> 기존 이미지 유지")
        void testUpdateReview_RemoveImageFalse_FileNull() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            // file is null
            MultipartFile nullFile = null;

            // when
            boolean result = reviewService.updateReview(userId, reviewRequestDto, nullFile, false);

            // then
            assertTrue(result); // 메서드 결과 확인
            verify(bookRepository).findById(book.getId()); // 도서 조회 확인
            verify(reviewRepository).findByBookAndUserId(book, userId); // 리뷰 조회 확인
            verify(objectService).generateAuthToken(); // 인증 토큰 호출 확인

            // 업로드/삭제가 호출되지 않았는지 확인
            verify(objectService, never()).uploadObject(anyString(), anyString(), any(InputStream.class));
            verify(objectService, never()).deleteObject(anyString(), anyString());

            // 리뷰 엔티티의 텍스트와 평점이 업데이트되었는지 확인
            assertEquals(reviewRequestDto.getText(), review.getText());
            assertEquals(reviewRequestDto.getRating(), review.getRating());

            // 기존 이미지 URL이 유지되었는지 확인
            assertEquals("testImageUrl", review.getImageUrl());
        }

        @Test
        @DisplayName("isRemoveImage=false && 파일이 empty -> 기존 이미지 유지")
        void testUpdateReview_RemoveImageFalse_FileEmpty() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            // file is empty
            when(multipartFile.isEmpty()).thenReturn(true);

            // when
            boolean result = reviewService.updateReview(userId, reviewRequestDto, multipartFile, false);

            // then
            assertTrue(result); // 메서드 결과 확인
            verify(bookRepository).findById(book.getId()); // 도서 조회 확인
            verify(reviewRepository).findByBookAndUserId(book, userId); // 리뷰 조회 확인
            verify(objectService).generateAuthToken(); // 인증 토큰 호출 확인

            // 업로드/삭제가 호출되지 않았는지 확인
            verify(objectService, never()).uploadObject(anyString(), anyString(), any(InputStream.class));
            verify(objectService, never()).deleteObject(anyString(), anyString());

            // 리뷰 엔티티의 텍스트와 평점이 업데이트되었는지 확인
            assertEquals(reviewRequestDto.getText(), review.getText());
            assertEquals(reviewRequestDto.getRating(), review.getRating());

            // 기존 이미지 URL이 유지되었는지 확인
            assertEquals("testImageUrl", review.getImageUrl());
        }


        @Test
        @DisplayName("파일 getInputStream()에서 IOException 발생 -> RuntimeException 변환")
        void testUpdateReview_FileUploadIOException() throws IOException {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

            // 시나리오: removeImage=false + 파일 존재 → 업로드 시도 중 IOException
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getInputStream()).thenThrow(new IOException("Test IOException"));

            doNothing().when(objectService).generateAuthToken();

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> reviewService.updateReview(userId, reviewRequestDto, multipartFile, false)
            );
            assertTrue(ex.getMessage().contains("이미지 수정 실패"));

            verify(objectService).generateAuthToken();
            // 업로드가 시도되다가 실패
            verify(objectService, never()).uploadObject(anyString(), anyString(), any(InputStream.class));
            // deleteObject도 호출되지 않음
            verify(objectService, never()).deleteObject(anyString(), anyString());
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
    @Nested
    @DisplayName("getAllReviewsByBookId 메소드 테스트")
    class GetAllReviewsByBookIdTest {

        @Test
        @DisplayName("성공적으로 모든 리뷰 조회")
        void testGetAllReviewsByBookId_Success() {
            // given
            List<Review> reviews = Arrays.asList(review, review, review);
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findAllByBookOrderByCreatedAtDesc(book)).thenReturn(reviews);

            // when
            List<ReviewResponseDto> result = reviewService.getAllReviewsByBookId(book.getId());

            // then
            assertNotNull(result);
            assertEquals(3, result.size());
            for (ReviewResponseDto responseDto : result) {
                assertEquals(review.getUserId(), responseDto.getUserId());
                assertEquals(review.getText(), responseDto.getText());
                assertEquals(review.getRating(), responseDto.getRating());
                assertEquals(review.getCreatedAt(), responseDto.getCreatedAt());
                assertEquals(review.getImageUrl(), responseDto.getImageUrl());
            }
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).findAllByBookOrderByCreatedAtDesc(book);
        }

        @Test
        @DisplayName("책이 존재하지 않아 BookNotFoundException 발생")
        void testGetAllReviewsByBookId_BookNotFound() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(BookNotFoundException.class, () -> reviewService.getAllReviewsByBookId(book.getId()));
            verify(reviewRepository, never()).findAllByBookOrderByCreatedAtDesc(any());
        }

        @Test
        @DisplayName("책은 존재하나 리뷰가 없는 경우 빈 리스트 반환")
        void testGetAllReviewsByBookId_NoReviews() {
            // given
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(reviewRepository.findAllByBookOrderByCreatedAtDesc(book)).thenReturn(Collections.emptyList());

            // when
            List<ReviewResponseDto> result = reviewService.getAllReviewsByBookId(book.getId());

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(bookRepository).findById(book.getId());
            verify(reviewRepository).findAllByBookOrderByCreatedAtDesc(book);
        }
    }
}
