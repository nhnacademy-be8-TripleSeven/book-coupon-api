package com.nhnacademy.bookapi.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    private String indexes; // 목차 제목

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private Book book; // 도서 아이디


    public BookIndex(String index, Book book) {
        this.indexes = index;
        this.book = book;
    }

    public void create(String index, Book book) {
        this.indexes = index;
        this.book = book;
    }

    public void updateIndexText(String indexText) {
        this.indexes = indexText;
    }

}
