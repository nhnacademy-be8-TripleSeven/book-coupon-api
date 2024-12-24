package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Entity
public class BookCreatorMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private BookCreator creator;

    public BookCreatorMap(Book book, BookCreator creator) {
        this.book = book;
        this.creator = creator;
    }

    public void create(Book book, BookCreator creator) {
        this.book = book;
        this.creator = creator;
    }
}
