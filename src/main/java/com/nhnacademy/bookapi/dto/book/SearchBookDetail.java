package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDetail;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Image;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchBookDetail {

    private String title;
    private String description;
    private LocalDate publishedDate;
    private int regularPrice;
    private int salePrice;
    private String isbn13;
    private int stock;
    private int page;
    private String coverUrl;
    private String publisher;
    private String bookIndex;
    //private List<Image> bookImages;
    private List<String> detailImages;
    private List<BookCreatorDetail> bookCreators;
    private StringBuilder categories;
    private StringBuilder tags;
    private StringBuilder bookTypes;

    public SearchBookDetail(String title, String description, LocalDate publishedDate,
        int regularPrice,
        int salePrice, String isbn13, int stock, int page, String coverUrl, String publisher) {
        this.title = title;
        this.description = description;
        this.publishedDate = publishedDate;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.isbn13 = isbn13;
        this.stock = stock;
        this.page = page;
        this.coverUrl = coverUrl;
        this.publisher = publisher;
    }
}
