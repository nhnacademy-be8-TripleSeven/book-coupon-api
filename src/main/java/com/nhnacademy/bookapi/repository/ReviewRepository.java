package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBookAndOrderDetailId(Book book, Long orderDetailId);
    Optional<Review> findByBookAndOrderDetailId(Book book, Long orderDetailId);
}
