package com.nhnacademy.bookapi.dto.book;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class UpdateBookRequest {


    private String title;
    private long bookId;
    private String category;
    private LocalDate publishedDate;
    private String bookIntroduction;
    private int price;

}
