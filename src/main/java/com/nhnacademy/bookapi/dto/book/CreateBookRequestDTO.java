package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.entity.Book;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateBookRequestDTO {

    private String title;
    private String author;
    private String isbn; //isbn unique처리
    private String publisher;
    private String description;
    private int pages;
    private LocalDate publicationDate;
    private int regularPrice;
    private int salePrice;
    private int stock;
    private String imageUrl;





}
