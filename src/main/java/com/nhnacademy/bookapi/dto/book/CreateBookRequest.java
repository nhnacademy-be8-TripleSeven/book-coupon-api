package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.entity.Book;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class CreateBookRequest {

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


    //이걸 static말고 메서드로 처리
    public static Book createBook(CreateBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setStock(request.getStock());
        book.setDescription(request.getDescription());
        book.setPage(request.getPages());
        book.setSalePrice(request.getSalePrice());
        book.setRegularPrice(request.getRegularPrice());
        book.setIsbn13(request.getIsbn());
        return book;
    }


}
