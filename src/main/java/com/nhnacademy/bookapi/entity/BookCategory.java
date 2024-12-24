package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Category category;


    public BookCategory(Book book, Category category) {
        this.book = book;
        this.category = category;
    }

    public void create(Book book, Category category) {
        this.book = book;
        this.category = category;
    }

    public void update(Book book, Category category) {
        this.book = book;
        this.category = category;
    }
}
