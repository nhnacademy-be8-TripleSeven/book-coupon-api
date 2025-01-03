package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class BookCreatorMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private BookCreator creator;

    public BookCreatorMap(Book book, BookCreator creator) {
        this.book = book;
        this.creator = creator;
    }

    public void create(Book book, BookCreator creator) {
        this.book = book;
        this.creator = creator;
    }

    public static BookCreatorMap update(Book book, BookCreator creator) {

        return new BookCreatorMap(book, creator);
    }
}
