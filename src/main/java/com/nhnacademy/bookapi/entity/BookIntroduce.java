package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

    @OneToOne
    private Book book;

    private BookIntroduce(String description, Book book) {
        this.description = description;
        this.book = book;
    }


    public static BookIntroduce bookIntroduceCreate(String description, Book book) {
        return new BookIntroduce(description, book);
    }
}
