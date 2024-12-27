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
import org.aspectj.apache.bcel.generic.LineNumberGen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;

//
//@Service
//@Transactional
//public class ReviewService {
//
//    private ReviewRepository reviewRepository;
//    private BookRepository bookRepository;
//
//    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
//        this.reviewRepository = reviewRepository;
//        this.bookRepository = bookRepository;
//    }
//
//    public boolean addReview(ReviewRequestDto reviewRequestDto) {
//        Book book = getBook(reviewRequestDto.getBookId());
//
//        if (reviewRepository.findByBookAndUserId(book, reviewRequestDto.getUserId()).isPresent()) {
//            throw new ReviewAlreadyExistException("Review already exists");
//        }
//
//        Review review = new Review(
//                reviewRequestDto.getText(),
//                LocalDateTime.now(),
//                reviewRequestDto.getRating(),
//                book,
//                reviewRequestDto.getUserId()
//        );
//
//        reviewRepository.save(review);
//        return true;
//    }
//
//    // userId와 bookId를 통해 수정하려는 컬럼을 찾고 업데이트
//    public boolean updateReview(ReviewRequestDto reviewRequestDto) {
//        Book book = getBook(reviewRequestDto.getBookId());
//        Review review = getReview(book, reviewRequestDto.getUserId());
//        review.setText(reviewRequestDto.getText());
//        review.setRating(reviewRequestDto.getRating());
//        return true;
//    }
//
//    //userId와 bookId를 통해 컬럼 삭제
//    public boolean deleteReview(ReviewRequestDto reviewRequestDto) {
//        Book book = getBook(reviewRequestDto.getBookId());
//        Review review = getReview(book, reviewRequestDto.getUserId());
//        reviewRepository.delete(review);
//        return true;
//    }
//
//    //한 유저가 쓴 모든 리뷰를 select
//    public List<Review> getAllReviewsByUserId(Long userId) {
//        return reviewRepository.findAllByUserId(userId);
//    }
//
//    private Book getBook(Long bookId) {
//        return bookRepository.findById(bookId)
//                .orElseThrow(() -> new BookNotFoundException("Book not found"));
//    }
//
//    private Review getReview(Book book, Long userId) {
//        return reviewRepository.findByBookAndUserId(book, userId)
//                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
//    }
//}
@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

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
            result.add(new ReviewResponseDto(review.getText(), review.getRating(), review.getCreatedAt()));
        }

        return result;
    }
    // 도서에 달려있는 개인 리뷰 조회
    public ReviewResponseDto getReview(Long bookId, Long userId) {
        Book book = getBook(bookId);
        Review review = getReview(book, userId);
        return new ReviewResponseDto(review.getText(), review.getRating(), review.getCreatedAt());
    }
    // 도서에 달려있는 모든 리뷰 조회
    public Page<ReviewResponseDto> getPagedReviewsByBookId(Long bookId, Pageable pageable) {
        Book book = getBook(bookId);
        Page<Review> reviews = reviewRepository.findAllByBookOrderByCreatedAtDesc(book, pageable);
        return reviews.map(review ->
                new ReviewResponseDto(review.getText(), review.getRating(), review.getCreatedAt()));
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
