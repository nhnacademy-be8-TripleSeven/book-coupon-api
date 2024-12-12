package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class BookIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 인덱스 아이디

    @Column(length = 50)
    @Setter
    private String title; // 목차 제목

    @Setter
    private int number; // 페이지 번호

    @Setter
    private int sequence; // 순서

    @Setter
    @ManyToOne
    private Book book; // 도서 아이디

    public BookIndex(String title, int number, int sequence, Book book) {
        this.title = title;
        this.number = number;
        this.sequence = sequence;
        this.book = book;
    }
}
