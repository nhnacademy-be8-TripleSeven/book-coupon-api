package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class BookType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Type types;

    private int ranks;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    public BookType(Type types, int ranks, Book book) {
        this.types = types;
        this.ranks = ranks;
        this.book = book;
    }

    public void create(Type types, int ranks, Book book) {
        this.types = types;
        this.ranks = ranks;
        this.book = book;
    }

    public void update(Type types, int ranks, Book book) {
        this.types = types;
        this.ranks = ranks;
        this.book = book;
    }
}
