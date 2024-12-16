package com.nhnacademy.bookapi.service.review;

import com.nhnacademy.bookapi.dto.review.ReviewRequestDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.ReviewAlreadyExistException;
import com.nhnacademy.bookapi.exception.ReviewNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    private ReviewRepository reviewRepository;
    private BookRepository bookRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addReview(ReviewRequestDto reviewRequestDto) {
        Book book = getBook(reviewRequestDto.getBookId());

        if (reviewRepository.findByBookAndUserId(book, reviewRequestDto.getUserId()).isPresent()) {
            throw new ReviewAlreadyExistException("Review already exists");
        }

        Review review = new Review(
                reviewRequestDto.getText(),
                LocalDateTime.now(),
                reviewRequestDto.getRating(),
                book,
                reviewRequestDto.getUserId()
        );

        reviewRepository.save(review);
        return true;
    }

    // userId와 bookId를 통해 수정하려는 컬럼을 찾고 업데이트
    public boolean updateReview(ReviewRequestDto reviewRequestDto) {
        Book book = getBook(reviewRequestDto.getBookId());
        Review review = getReview(book, reviewRequestDto.getUserId());
        review.setText(reviewRequestDto.getText());
        review.setRating(reviewRequestDto.getRating());
        return true;
    }

    //userId와 bookId를 통해 컬럼 삭제
    public boolean deleteReview(ReviewRequestDto reviewRequestDto) {
        Book book = getBook(reviewRequestDto.getBookId());
        Review review = getReview(book, reviewRequestDto.getUserId());
        reviewRepository.delete(review);
        return true;
    }

    //한 유저가 쓴 모든 리뷰를 select
    public List<Review> getAllReviewsByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId);
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
