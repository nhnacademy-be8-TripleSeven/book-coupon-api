package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;
    private LocalDate publishDate;
    private int regularPrice;
    private int salePrice;
    private String isbn13;
    private int stock;
    private int page;

//    @ManyToOne
//    private Publisher publisher;

    public Book(String title, String description, LocalDate publishDate, int regularPrice, int salePrice, String isbn3, int stock, int page) {
        this.title = title;
        this.description = description;
        this.publishDate = publishDate;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.isbn13 = isbn3;
        this.stock = stock;
        this.page = page;
    }
}
