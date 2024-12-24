package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text; // 리뷰내용 review_text

    @Column(nullable = false)
    private LocalDateTime createdAt; // 리뷰 작성일 created_at, 시분초까지 필요함

    @Column(nullable = false)
    private int rating; // 평점 book_rating

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Book book; // 도서 아이디

    @Column(nullable = false)
    private Long userId;

    public Review(String text, LocalDateTime createdAt, int rating, Book book, Long userId) {
        this.text = text;
        this.createdAt = createdAt;
        this.rating = rating;
        this.book = book;
        this.userId = userId;
    }

    public void updateText(String text) {
        this.text = text;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }

    public void updateCreatedAT(LocalDateTime now) {
        this.createdAt = now;
    }
}
