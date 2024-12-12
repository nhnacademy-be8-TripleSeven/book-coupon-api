package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.entity.BookCreator;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchBookDTO {

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
    private List<BookCreator> bookCreators;
}
