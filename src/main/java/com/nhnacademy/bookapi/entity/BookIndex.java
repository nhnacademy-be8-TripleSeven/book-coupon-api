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

    @Column(columnDefinition = "TEXT", nullable = true)
    @Setter
    private String indexText; // 목차 제목

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "book_id")
    private Book book; // 도서 아이디

    @Setter
    private int sequence;

    public BookIndex(String indexText, Book book, int sequence) {
        this.indexText = indexText;
        this.book = book;
        this.sequence = sequence;
    }

    public void updateIndexText(String indexText) {
        this.indexText = indexText;
    }

    public void updateSequence(int sequence) {
        this.sequence = sequence;
    }
}
