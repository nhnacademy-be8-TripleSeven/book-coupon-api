package com.nhnacademy.bookapi.repository.likes;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Likes;
import com.nhnacademy.bookapi.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeRepositoryTest {
    @Mock
    private LikeRepository likeRepository;

    private Book book;
    private Book newBook;
    private Likes like;
    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        book = new Book();
        newBook = new Book();
        like = new Likes(book, userId, LocalDateTime.now());
    }

    @Test
    @DisplayName("findByBookAndUserId - 데이터가 존재할 때")
    void testFindByBookAndUserId_Found() {
        when(likeRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(like));

        Optional<Likes> result = likeRepository.findByBookAndUserId(book, userId);

        assertThat(result).isPresent();
        verify(likeRepository, times(1)).findByBookAndUserId(book, userId);
    }

    @Test
    @DisplayName("findByBookAndUserId - 데이터가 존재하지 않을 때")
    void testFindByBookAndUserId_NotFound() {
        when(likeRepository.findByBookAndUserId(any(Book.class), anyLong()))
                .thenReturn(Optional.empty());

        Optional<Likes> result = likeRepository.findByBookAndUserId(book, userId);

        assertThat(result).isEmpty();

        verify(likeRepository, times(1)).findByBookAndUserId(book, userId);
    }

    @Test
    @DisplayName("findAllByUserIdWithBook - 결과가 존재하는 경우")
    void testFindAllByUserIdWithBook_Found() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Likes> page = new PageImpl<>(Collections.singletonList(like), pageable, 1);
        when(likeRepository.findAllByUserIdWithBook(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        Page<Likes> result = likeRepository.findAllByUserIdWithBook(userId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(like);

        verify(likeRepository, times(1))
                .findAllByUserIdWithBook(userId, pageable);
    }

    @Test
    @DisplayName("findAllByUserIdWithBook - 결과가 없는 경우(Empty)")
    void testFindAllByUserIdWithBook_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Likes> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(likeRepository.findAllByUserIdWithBook(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<Likes> result = likeRepository.findAllByUserIdWithBook(userId, pageable);

        assertThat(result).isEmpty();

        verify(likeRepository, times(1))
                .findAllByUserIdWithBook(userId, pageable);
    }

    @Test
    @DisplayName("existsByBookAndUserId - 존재할 때 true 반환")
    void testExistsByBookAndUserId_Found() {
        when(likeRepository.existsByBookAndUserId(book, userId)).thenReturn(true);

        boolean exists = likeRepository.existsByBookAndUserId(book, userId);

        assertThat(exists).isTrue();

        verify(likeRepository, times(1))
                .existsByBookAndUserId(book, userId);
    }

    @Test
    @DisplayName("existsByBookAndUserId - 존재하지 않을 때 false 반환")
    void testExistsByBookAndUserId_NotFound() {
        when(likeRepository.existsByBookAndUserId(newBook, userId)).thenReturn(false);

        boolean notExists = likeRepository.existsByBookAndUserId(newBook, userId);

        assertThat(notExists).isFalse();

        verify(likeRepository, times(1))
                .existsByBookAndUserId(newBook, userId);
    }

    @Test
    @DisplayName("findAllByUserIdAndBookTitleContaining - 결과가 있을 때")
    void testFindAllByUserIdAndBookTitleContaining_Found() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Likes> page = new PageImpl<>(Collections.singletonList(like), pageable, 1);

        when(likeRepository.findAllByUserIdAndBookTitleContaining(eq(userId), eq("Test"), any(Pageable.class)))
                .thenReturn(page);

        Page<Likes> result = likeRepository.findAllByUserIdAndBookTitleContaining(userId, "Test", pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(likeRepository, times(1))
                .findAllByUserIdAndBookTitleContaining(userId, "Test", pageable);
    }

    @Test
    @DisplayName("findAllByUserIdAndBookTitleContaining - 결과가 없을 때 Empty 반환")
    void testFindAllByUserIdAndBookTitleContaining_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Likes> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(likeRepository.findAllByUserIdAndBookTitleContaining(eq(userId), eq("NonExisting"), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<Likes> result = likeRepository.findAllByUserIdAndBookTitleContaining(userId, "NonExisting", pageable);

        assertThat(result).isEmpty();

        verify(likeRepository, times(1))
                .findAllByUserIdAndBookTitleContaining(userId, "NonExisting", pageable);
    }

}
