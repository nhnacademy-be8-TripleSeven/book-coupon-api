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

    @Setter
    private String text; // 리뷰내용 review_text

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt; // 리뷰 작성일 created_at, 시분초까지 필요함

    @Column(nullable = false)
    @Setter
    private int rating; // 평점 book_rating

    @ManyToOne
    @JoinColumn(nullable = false)
    private Book book;

    @Column(nullable = false)
    private Long orderDetailId; // 주문 api에서 값을 받아와야 함

    public Review(String text, LocalDateTime createdAt, int rating, Book book, Long orderDetailId) {
        this.text = text;
        this.createdAt = createdAt;
        this.rating = rating;
        this.book = book;
        this.orderDetailId = orderDetailId;
    }

}
