package com.nhnacademy.bookapi.repository.review;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import com.nhnacademy.bookapi.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewRepositoryTest {

    @Mock
    private ReviewRepository reviewRepository;

    private Book book;
    private Book newBook;
    private Review review;
    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;

        book = new Book();

        newBook = new Book();

        review = new Review("This is a review", LocalDateTime.now(), 5, book, userId, "imageUrl");
    }

    @Test
    @DisplayName("existsByBookAndUserId - 존재할 때 true 반환")
    void testExistsByBookAndUserId_Found() {
        when(reviewRepository.existsByBookAndUserId(book, userId)).thenReturn(true);

        boolean result = reviewRepository.existsByBookAndUserId(book, userId);

        assertThat(result).isTrue();
        verify(reviewRepository, times(1))
                .existsByBookAndUserId(book, userId);
    }

    @Test
    @DisplayName("existsByBookAndUserId - 존재하지 않을 때 false 반환")
    void testExistsByBookAndUserId_NotFound() {
        when(reviewRepository.existsByBookAndUserId(newBook, userId)).thenReturn(false);

        boolean result = reviewRepository.existsByBookAndUserId(newBook, userId);

        assertThat(result).isFalse();
        verify(reviewRepository, times(1))
                .existsByBookAndUserId(newBook, userId);
    }

    @Test
    @DisplayName("findByBookAndUserId - 데이터가 존재할 때")
    void testFindByBookAndUserId_Found() {
        when(reviewRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(review));

        Optional<Review> result = reviewRepository.findByBookAndUserId(book, userId);

        assertThat(result).isPresent();
        assertThat(result.get().getBook()).isEqualTo(book);
        assertThat(result.get().getUserId()).isEqualTo(userId);

        verify(reviewRepository, times(1))
                .findByBookAndUserId(book, userId);
    }

    @Test
    @DisplayName("findByBookAndUserId - 데이터가 존재하지 않을 때")
    void testFindByBookAndUserId_NotFound() {
        when(reviewRepository.findByBookAndUserId(any(Book.class), anyLong()))
                .thenReturn(Optional.empty());

        Optional<Review> result = reviewRepository.findByBookAndUserId(book, userId);

        assertThat(result).isEmpty();
        verify(reviewRepository, times(1))
                .findByBookAndUserId(book, userId);
    }

    @Test
    @DisplayName("findAllByUserIdOrderByCreatedAtDesc - 결과가 존재하는 경우")
    void testFindAllByUserIdOrderByCreatedAtDesc_Found() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(review);

        when(reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(reviews);

        List<Review> result = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(review);

        verify(reviewRepository, times(1))
                .findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("findAllByUserIdOrderByCreatedAtDesc - 결과가 없는 경우(Empty)")
    void testFindAllByUserIdOrderByCreatedAtDesc_Empty() {
        when(reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Collections.emptyList());

        List<Review> result = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        assertThat(result).isEmpty();

        verify(reviewRepository, times(1))
                .findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("findAllByBookOrderByCreatedAtDesc (Page) - 결과가 존재하는 경우")
    void testFindAllByBookOrderByCreatedAtDesc_Found_Page() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(reviewRepository.findAllByBookOrderByCreatedAtDesc(eq(book), any(Pageable.class)))
                .thenReturn(reviewPage);

        Page<Review> result = reviewRepository.findAllByBookOrderByCreatedAtDesc(book, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst()).isEqualTo(review);

        verify(reviewRepository, times(1))
                .findAllByBookOrderByCreatedAtDesc(book, pageable);
    }

    @Test
    @DisplayName("findAllByBookOrderByCreatedAtDesc (Page) - 결과가 없는 경우(Empty)")
    void testFindAllByBookOrderByCreatedAtDesc_Empty_Page() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Review> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(reviewRepository.findAllByBookOrderByCreatedAtDesc(eq(book), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<Review> result = reviewRepository.findAllByBookOrderByCreatedAtDesc(book, pageable);

        assertThat(result).isEmpty();
        verify(reviewRepository, times(1))
                .findAllByBookOrderByCreatedAtDesc(book, pageable);
    }

    @Test
    @DisplayName("findAllByBookOrderByCreatedAtDesc (List) - 결과가 존재하는 경우")
    void testFindAllByBookOrderByCreatedAtDesc_Found_List() {
        when(reviewRepository.findAllByBookOrderByCreatedAtDesc(book))
                .thenReturn(List.of(review));

        List<Review> result = reviewRepository.findAllByBookOrderByCreatedAtDesc(book);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(review);

        verify(reviewRepository, times(1))
                .findAllByBookOrderByCreatedAtDesc(book);
    }

    @Test
    @DisplayName("findAllByBookOrderByCreatedAtDesc (List) - 결과가 없는 경우(Empty)")
    void testFindAllByBookOrderByCreatedAtDesc_Empty_List() {
        when(reviewRepository.findAllByBookOrderByCreatedAtDesc(book))
                .thenReturn(Collections.emptyList());

        List<Review> result = reviewRepository.findAllByBookOrderByCreatedAtDesc(book);

        assertThat(result).isEmpty();
        verify(reviewRepository, times(1))
                .findAllByBookOrderByCreatedAtDesc(book);
    }

    @Test
    @DisplayName("deleteByBookId - 메서드 정상 호출")
    void testDeleteByBookId() {
        Long bookId = book.getId();
        doNothing().when(reviewRepository).deleteByBookId(bookId);

        reviewRepository.deleteByBookId(bookId);

        verify(reviewRepository, times(1))
                .deleteByBookId(bookId);
    }

    @Test
    @DisplayName("findAllUserIdsByBookId - 결과가 존재하는 경우")
    void testFindAllUserIdsByBookId_Found() {
        Long bookId = book.getId();
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        when(reviewRepository.findAllUserIdsByBookId(bookId))
                .thenReturn(userIds);

        List<Long> result = reviewRepository.findAllUserIdsByBookId(bookId);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(1L, 2L, 3L);

        verify(reviewRepository, times(1))
                .findAllUserIdsByBookId(bookId);
    }

    @Test
    @DisplayName("findAllUserIdsByBookId - 결과가 없는 경우(Empty)")
    void testFindAllUserIdsByBookId_Empty() {
        Long bookId = book.getId();
        when(reviewRepository.findAllUserIdsByBookId(bookId))
                .thenReturn(Collections.emptyList());

        List<Long> result = reviewRepository.findAllUserIdsByBookId(bookId);

        assertThat(result).isEmpty();
        verify(reviewRepository, times(1))
                .findAllUserIdsByBookId(bookId);
    }

    @Test
    @DisplayName("existsByBookId - 존재할 경우 true 반환")
    void testExistsByBookId_Found() {
        when(reviewRepository.existsByBookId(book.getId()))
                .thenReturn(true);

        boolean result = reviewRepository.existsByBookId(book.getId());

        assertThat(result).isTrue();
        verify(reviewRepository, times(1))
                .existsByBookId(book.getId());
    }

    @Test
    @DisplayName("existsByBookId - 존재하지 않을 경우 false 반환")
    void testExistsByBookId_NotFound() {
        when(reviewRepository.existsByBookId(newBook.getId()))
                .thenReturn(false);

        boolean result = reviewRepository.existsByBookId(newBook.getId());

        assertThat(result).isFalse();
        verify(reviewRepository, times(1))
                .existsByBookId(newBook.getId());
    }

}
