package com.nhnacademy.bookapi.service.likes;

import com.nhnacademy.bookapi.dto.likes.LikesResponseDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Likes;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.LikeAlreadyExistException;
import com.nhnacademy.bookapi.exception.LikeNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.LikeRepository;
import com.nhnacademy.bookapi.service.likes.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LikesServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddLikeSuccess() {
        Long userId = 1L;
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(likeRepository.existsByBookAndUserId(book, userId)).thenReturn(false);

        likeService.addLike(userId, bookId);
        verify(likeRepository, times(1)).save(any(Likes.class));
    }

    @Test
    void testAddLikeAlreadyExists() {
        Long userId = 1L;
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(likeRepository.existsByBookAndUserId(book, userId)).thenReturn(true);

        assertThrows(LikeAlreadyExistException.class, () -> likeService.addLike(userId, bookId));
    }

    @Test
    void testIsLikedSuccess() {
        Long userId = 1L;
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(likeRepository.existsByBookAndUserId(book, userId)).thenReturn(true);

        boolean result = likeService.isLiked(userId, bookId);

        assertTrue(result);
    }

    @Test
    void testIsLikedNotFoundException() {
        Long userId = 1L;
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(likeRepository.existsByBookAndUserId(book, userId)).thenReturn(false);
        boolean result = likeService.isLiked(userId, bookId);
        assertFalse(result);
    }

    @Test
    void deleteLikeSuccess() {
        Long userId = 1L;
        Long bookId = 1L;
        Book book = new Book();
        Likes likes = new Likes(book, userId, LocalDateTime.now());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(likeRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.of(likes));

        likeService.deleteLike(userId, bookId);

        verify(likeRepository, times(1)).delete(likes);
    }

    @Test
    void deleteLikeNotFoundException() {
        Long userId = 1L;
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(likeRepository.findByBookAndUserId(book, userId)).thenReturn(Optional.empty());

        assertThrows(LikeNotFoundException.class, () -> likeService.deleteLike(userId, bookId));
    }

    @Test
    void getPagedLikesByUserIdAndKeywordSuccess() {
        // Given
        Long userId = 1L;
        String keyword = "keyword";
        Pageable pageable = PageRequest.of(0, 10);
        Book book = new Book();
        book.setTestTitle("keyword");
        Likes like = new Likes(book, userId, LocalDateTime.now());
        Page<Likes> likesPage = new PageImpl<>(Collections.singletonList(like));

        when(likeRepository.findAllByUserIdAndBookTitleContaining(userId, keyword, pageable)).thenReturn(likesPage);

        // When
        Page<LikesResponseDto> result = likeService.getPagedLikesByUserIdAndKeyword(userId, keyword, pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("keyword", result.getContent().get(0).getBookTitle());
    }

    @Test
    void getBookNotFound() {
        // Given
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> likeService.isLiked(1L, bookId));
    }
}
