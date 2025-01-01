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

}
