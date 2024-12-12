package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Tag tag;

    @ManyToOne
    private Book book;

    public BookTag(){}

    public BookTag(Book book, Tag tag) {
        this.book = book;
        this.tag = tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Tag getTag() {
        return this.tag;
    }

    public Book getBook() {
        return this.book;
    }
}
