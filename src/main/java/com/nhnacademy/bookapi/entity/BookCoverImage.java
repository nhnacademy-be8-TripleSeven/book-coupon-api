package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
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

    @ManyToOne
    private Image image;

    @ManyToOne
    private Book book;


    public static BookCoverImage bookCoverImageMapper(Image image, Book book) {
        BookCoverImage bookCoverImage = new BookCoverImage();

        bookCoverImage.image = image;
        bookCoverImage.book = book;
        return bookCoverImage;
    }
}
