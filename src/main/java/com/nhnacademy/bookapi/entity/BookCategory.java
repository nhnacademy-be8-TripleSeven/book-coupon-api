package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;


    public BookCategory(Book book, Category category) {
        this.book = book;
        this.category = category;
    }

    public void create(Book book, Category category) {
        this.book = book;
        this.category = category;
    }

    public static BookCategory update(Book book, Category category) {
        return new BookCategory(book, category);
    }
}
