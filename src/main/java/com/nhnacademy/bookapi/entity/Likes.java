package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Book book;

    @Column(nullable = false)
    private Long userId;

    private LocalDateTime createdAt;

    public Likes(Book book, Long userId, LocalDateTime createdAt) {
        this.book = book;
        this.userId = userId;
        this.createdAt = createdAt;
    }

}
