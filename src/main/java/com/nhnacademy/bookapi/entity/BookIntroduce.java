package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BookIntroduce {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    public BookIntroduce(String description, Book book) {
        this.description = description;
        this.book = book;
    }


    public void updateIntroduce(String description, Book book) {
        this.description = description;
        this.book = book;
    }
}
