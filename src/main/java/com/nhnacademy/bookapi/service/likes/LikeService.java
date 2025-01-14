package com.nhnacademy.bookapi.service.likes;

import com.nhnacademy.bookapi.dto.likes.LikesRequestDto;
import com.nhnacademy.bookapi.dto.likes.LikesResponseDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Likes;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.LikeAlreadyExistException;
import com.nhnacademy.bookapi.exception.LikeNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.LikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;

    public LikeService(LikeRepository likeRepository, BookRepository bookRepository) {
        this.likeRepository = likeRepository;
        this.bookRepository = bookRepository;
    }

    // 좋아요 추가
    public void addLike(Long userId, Long bookId) {
        Book book = getBook(bookId);

        if (likeRepository.existsByBookAndUserId(book, userId)) {
            throw new LikeAlreadyExistException("The User is already liked");
        }
        Likes like = new Likes(book, userId, LocalDateTime.now());
        likeRepository.save(like);
    }

    public boolean isLiked(Long userId, Long bookId) {
        Book book = getBook(bookId);
        return likeRepository.existsByBookAndUserId(book, userId);
    }

    // 좋아요 삭제
    public void deleteLike(Long userId, Long bookId) {
        Book book = getBook(bookId);
        Likes like = getLikes(book, userId);
        likeRepository.delete(like);
    }

    public Page<LikesResponseDto> getPagedLikesByUserIdAndKeyword(Long userId, String keyword, Pageable pageable) {
        Page<Likes> likesPage = likeRepository.findAllByUserIdAndBookTitleContaining(userId, keyword, pageable);

        return likesPage.map(likes -> new LikesResponseDto(likes.getBook().getId(), likes.getBook().getTitle(), likes.getCreatedAt()));
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    private Likes getLikes(Book book, Long userId) {
        return likeRepository.findByBookAndUserId(book, userId).orElseThrow(() -> new LikeNotFoundException("Like not found"));
    }
}