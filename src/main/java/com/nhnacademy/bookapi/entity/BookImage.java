package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class BookImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Image image;

    public BookImage(Book book, Image image) {
        this.book = book;
        this.image = image;
    }
}
