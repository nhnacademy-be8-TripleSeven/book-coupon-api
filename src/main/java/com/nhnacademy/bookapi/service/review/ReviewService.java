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
import org.aspectj.apache.bcel.generic.LineNumberGen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

//    private final String storageUrl = "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e";
//    private final String authUrl = "https://api-identity.infrastructure.cloud.toast.com/v2.0/tokens";
//    private final String tenantId = "c20e3b10d61749a2a52346ed0261d79e";
//    private final String username = "rlgus4531@naver.com";
//    private final String password = "team3";
//    private final String containerName = "triple-seven";

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addReview(Long userId, ReviewRequestDto reviewRequestDto) {
        Book book = getBook(reviewRequestDto.getBookId());

        if (reviewRepository.existsByBookAndUserId(book, userId)) {
            throw new ReviewAlreadyExistException("You are already reviewed on this book");
        }

        Review review = new Review(
                reviewRequestDto.getText(),
                LocalDateTime.now(),
                reviewRequestDto.getRating(),
                book,
                userId
        );

        reviewRepository.save(review);
        return true;
    }

    public boolean updateReview(Long userId, ReviewRequestDto reviewRequestDto) {
        Book book = getBook(reviewRequestDto.getBookId());
        Review review = getReview(book, userId);
        review.updateText(reviewRequestDto.getText());
        review.updateRating(reviewRequestDto.getRating());
        review.updateCreatedAT(LocalDateTime.now());
        return true;
    }

    public boolean deleteReview(Long userId, Long bookId) {
        Book book = getBook(bookId);
        Review review = getReview(book, userId);
        reviewRepository.delete(review);
        return true;
    }
    // 유저가 쓴 모든 리뷰 조회
    public List<ReviewResponseDto> getAllReviewsByUserId(Long userId) {
        List<ReviewResponseDto> result = new ArrayList<>();
        List<Review> reviews = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        for (Review review : reviews) {
            result.add(new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt()));
        }

        return result;
    }
    // 도서에 달려있는 개인 리뷰 조회
    public ReviewResponseDto getReview(Long bookId, Long userId) {
        Book book = getBook(bookId);
        Review review = getReview(book, userId);
        return new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt());
    }
    // 도서에 달려있는 리뷰 페이징 처리 메소드
    public Page<ReviewResponseDto> getPagedReviewsByBookId(Long bookId, Pageable pageable) {
        Book book = getBook(bookId);
        Page<Review> reviews = reviewRepository.findAllByBookOrderByCreatedAtDesc(book, pageable);
        return reviews.map(review ->
                new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt()));
    }

    // 도서에 달려있는 모든 리뷰 조회
    public List<ReviewResponseDto> getAllReviewsByBookId(Long bookId) {
        Book book = getBook(bookId);
        List<Review> reviews = reviewRepository.findAllByBookOrderByCreatedAtDesc(book);
        List<ReviewResponseDto> result = new ArrayList<>();
        for (Review review : reviews) {
            result.add(new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt()));
        }
        return result;
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    private Review getReview(Book book, Long userId) {
        return reviewRepository.findByBookAndUserId(book, userId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
    }
}
