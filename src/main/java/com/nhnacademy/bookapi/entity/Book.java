package com.nhnacademy.bookapi.entity;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Entity
@NoArgsConstructor
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(nullable = false)
    private int regularPrice;

    @Column(nullable = false)
    private int salePrice;

    @Column(nullable = false, length = 50, unique = true)
    private String isbn13;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int page;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Publisher publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BookCategory> bookCategories = new ArrayList<>();

    public Book(String title, String description, LocalDate publishDate, int regularPrice,
        int salePrice, String isbn13, int stock, int page) {
        this.title = title;
        this.description = description;
        this.publishDate = publishDate;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.isbn13 = isbn13;
        this.stock = stock;
        this.page = page;
    }

    public Book(String title, String description, LocalDate publishDate, int regularPrice,
        int salePrice, String isbn13, int stock, int page, Publisher publisher) {
        this.title = title;
        this.description = description;
        this.publishDate = publishDate;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.isbn13 = isbn13;
        this.stock = stock;
        this.page = page;
        this.publisher = publisher;
    }

    public void createPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void create(String title, String description, LocalDate publishDate, int regularPrice, int salePrice, String isbn13, int stock, int page, Publisher publisher) {
        this.title = title;
        this.description = description;
        this.publishDate = publishDate;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.isbn13 = isbn13;
        this.stock = stock;
        this.page = page;
        this.publisher = publisher;
    }

    public void publisherUpdate(Publisher publisher) {
        this.publisher = publisher;
    }

    public void updatePage(int page) {
        this.page = page;
    }



    public void update(String title, String isbn, LocalDate publishDate, int regularPrice, int salePrice, String description) {
        this.title = title;
        this.isbn13 = isbn;
        this.publishDate = publishDate;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.description = description;
    }

}

