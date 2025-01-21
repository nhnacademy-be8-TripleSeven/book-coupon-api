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
import com.nhnacademy.bookapi.service.object.NaverObjectStorageService;
import com.nhnacademy.bookapi.service.object.ObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ObjectService objectService;

    @Mock
    private NaverObjectStorageService naverObjectStorageService;

    private Book book;
    private Review review;
    private ReviewRequestDto reviewRequestDto;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        book = new Book();
        review = new Review("Great book!", LocalDateTime.now(), 5, book, 1L, "test-image-url");
        reviewRequestDto = new ReviewRequestDto("Amazing read!", 4, 1L);
        mockFile = mock(MultipartFile.class);
    }

    @Test
    void addReview_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.existsByBookAndUserId(any(), anyLong())).thenReturn(false);

        // 🛠️ Mock 객체로 NaverObjectStorageService의 메서드가 정상적으로 호출되도록 설정
        when(naverObjectStorageService.uploadFile(anyString(), any())).thenReturn("uploaded-image-url");

        when(reviewRepository.save(any())).thenReturn(review);

        boolean result = reviewService.addReview(1L, reviewRequestDto, mockFile);
        verify(reviewRepository, times(1)).save(any());
        assertEquals("uploaded-image-url", naverObjectStorageService.uploadFile(anyString(), any()));
        assertTrue(result);
    }

    @Test
    void addReview_noFile_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.existsByBookAndUserId(any(), anyLong())).thenReturn(false);

        when(reviewRepository.save(any())).thenReturn(review);

        // ✅ 파일을 null로 전달
        boolean result = reviewService.addReview(1L, reviewRequestDto, null);

        verify(reviewRepository, times(1)).save(any());
        verify(naverObjectStorageService, never()).uploadFile(anyString(), any()); // 파일 업로드가 호출되지 않는지 확인
        assertTrue(result);
    }

    @Test
    void addReview_alreadyExists() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.existsByBookAndUserId(any(), anyLong())).thenReturn(true);

        assertThrows(ReviewAlreadyExistException.class, () -> reviewService.addReview(1L, reviewRequestDto, mockFile));
    }

    @Test
    void updateReview_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookAndUserId(any(), anyLong())).thenReturn(Optional.of(review));
        when(naverObjectStorageService.uploadFile(anyString(), any())).thenReturn("new-image-url");

        boolean result = reviewService.updateReview(1L, reviewRequestDto, mockFile, false);
        assertTrue(result);
    }

    @Test
    void updateReview_reviewNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookAndUserId(any(), anyLong())).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(1L, reviewRequestDto, mockFile, false));
    }

    @Test
    void updateReview_removeOldImageWithoutNew_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookAndUserId(any(), anyLong())).thenReturn(Optional.of(review));

        boolean result = reviewService.updateReview(1L, reviewRequestDto, null, true);

        verify(naverObjectStorageService, times(1)).deleteFile(anyString()); // 기존 이미지 삭제 확인
        verify(naverObjectStorageService, never()).uploadFile(anyString(), any()); // 새로운 파일 업로드가 없어야 함
        assertTrue(result);
    }

    @Test
    void updateReview_keepExistingImage_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookAndUserId(any(), anyLong())).thenReturn(Optional.of(review));

        boolean result = reviewService.updateReview(1L, reviewRequestDto, null, false);

        verify(naverObjectStorageService, never()).uploadFile(anyString(), any()); // 파일 업로드 안 함
        verify(naverObjectStorageService, never()).deleteFile(anyString()); // 파일 삭제도 안 함
        assertTrue(result);
    }

    @Test
    void updateReview_uploadNewImageWithoutOld_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookAndUserId(any(), anyLong())).thenReturn(Optional.of(review));
        when(naverObjectStorageService.uploadFile(anyString(), any())).thenReturn("new-image-url");

        boolean result = reviewService.updateReview(1L, reviewRequestDto, mockFile, false);

        verify(naverObjectStorageService, times(1)).uploadFile(anyString(), any()); // 새로운 파일 업로드 확인
        verify(naverObjectStorageService, never()).deleteFile(anyString()); // 기존 이미지 삭제 없음
        assertTrue(result);
    }

    @Test
    void deleteAllReviewsWithBook_success() {
        when(reviewRepository.findAllUserIdsByBookId(anyLong())).thenReturn(List.of(1L, 2L));

        reviewService.deleteAllReviewsWithBook(1L);
        verify(reviewRepository, times(1)).deleteByBookId(1L);
        verify(naverObjectStorageService, times(2)).deleteFile(anyString());
    }

    @Test
    void getAllReviewsByUserId_success() {
        when(reviewRepository.findAllByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of(review));

        List<ReviewResponseDto> result = reviewService.getAllReviewsByUserId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getReview_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookAndUserId(any(), anyLong())).thenReturn(Optional.of(review));

        ReviewResponseDto result = reviewService.getReview(1L, 1L);
        assertNotNull(result);
    }

    @Test
    void getPagedReviewsByBookId_success() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBookOrderByCreatedAtDesc(any(), any())).thenReturn(new PageImpl<>(List.of(review)));

        Page<ReviewResponseDto> result = reviewService.getPagedReviewsByBookId(1L, pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllReviewsByBookId_success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBookOrderByCreatedAtDesc(any())).thenReturn(List.of(review));

        List<ReviewResponseDto> result = reviewService.getAllReviewsByBookId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getBook_notFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> reviewService.getPagedReviewsByBookId(1L, PageRequest.of(0, 10)));
    }
}
