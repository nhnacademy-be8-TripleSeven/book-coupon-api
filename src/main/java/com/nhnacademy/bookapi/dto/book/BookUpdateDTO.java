package com.nhnacademy.bookapi.dto.book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.ISBN;

@Getter
@AllArgsConstructor
public class BookUpdateDTO {

    private long id;
    private String title;
    private String isbn;
    private List<String> categories;
    private List<String> bookTypes;
    private List<String> authors;
    private List<String> tags;
    private LocalDate publishedDate;
    private String description;
    private int regularPrice;
    private int salePrice;
    private String index;
    private String coverImage;
    private String detailImage;

    public BookUpdateDTO(long id, String title, String isbn, String categories, String bookTypes,
        String bookCreators, String tags, LocalDate publishedDate,String description, int regularPrice
    , int salePrice, String index, String coverImage, String detailImage) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.categories = parser(categories);
        this.bookTypes = parser(bookTypes);
        this.authors = parser(bookCreators);
        this.tags = parser(tags);
        this.publishedDate = publishedDate;
        this.description = description;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.index = index;
        this.coverImage = coverImage;
        this.detailImage = detailImage;
    }




    private List<String> parser(String words){
        String[] split = words.split(",");
        return Arrays.stream(split.clone()).toList();
    }

}
