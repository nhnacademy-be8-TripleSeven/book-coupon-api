package com.nhnacademy.bookapi.service.review;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import com.nhnacademy.bookapi.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void addReview(Book book, Long orderDetailId, Long userId, String text, int rating) {
        if (Objects.isNull(orderDetailId)) { // 주문하지 않은 회원일 경우
            throw new IllegalArgumentException("You are not ordered this book");
        }
        if (reviewRepository.existsByBookAndOrderDetailId(book, orderDetailId)) {
            throw new IllegalArgumentException("You are already reviewed this book");
        }
        reviewRepository.save(new Review(text, LocalDateTime.now(), rating, book, orderDetailId));
    }

    public void updateReview(Book book, Long orderDetailId, String newText, int newRating) {
        if (!reviewRepository.existsByBookAndOrderDetailId(book, orderDetailId)) {
            throw new IllegalArgumentException("You are not reviewed this book");
        }
        Optional<Review> optionalReview = reviewRepository.findByBookAndOrderDetailId(book, orderDetailId); // 업데이트하려는 컬럼을 찾고
        Review review = null;
        if (optionalReview.isPresent()) {
            review = optionalReview.get();
        } else {
            throw new IllegalArgumentException("You are not reviewed this book");
        }
        review.setText(newText);
        review.setRating(newRating);
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    public void deleteReview(Book book, Long orderDetailId) {
        Optional<Review> optionalReview = reviewRepository.findByBookAndOrderDetailId(book, orderDetailId);
        if (optionalReview.isPresent()) {
            reviewRepository.delete(optionalReview.get());
        }
    }

}
