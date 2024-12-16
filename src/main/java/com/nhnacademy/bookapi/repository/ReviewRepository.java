package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBookAndUserId(Book book, Long userId);
    Optional<Review> findByBookAndUserId(Book book, Long userId);

    List<Review> findAllByUserId(Long id);
}
