package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBookAndUserId(Book book, Long userId);
    Optional<Review> findByBookAndUserId(Book book, Long userId);
    //@Query("select r from Review r join fetch r.book where r.userId = :userId")
    List<Review> findAllByUserIdOrderByCreatedAtDesc(Long id);

    Page<Review> findAllByBookOrderByCreatedAtDesc(Book book, Pageable pageable);
    List<Review> findAllByBookOrderByCreatedAtDesc(Book book);

    @Modifying
    @Query("delete from Review r where r.book.id =:id")
    void deleteByBookId(Long id);
}
