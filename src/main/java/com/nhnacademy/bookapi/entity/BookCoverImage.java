package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class BookCoverImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;


    public BookCoverImage(Image image, Book book) {
        this.image = image;
        this.book = book;
    }

    public void update(Image image, Book book) {
        this.image = image;
       this.book = book;
  }
}
