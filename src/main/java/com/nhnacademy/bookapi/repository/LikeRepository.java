package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Likes;
import org.hibernate.annotations.Fetch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByBookAndUserId(Book book, Long userId);

    @Query("SELECT l FROM Likes l JOIN FETCH l.book WHERE l.userId = :userId")
    Page<Likes> findAllByUserIdWithBook(Long userId, Pageable pageable);

    boolean existsByBookAndUserId(Book book, Long userId);
}
