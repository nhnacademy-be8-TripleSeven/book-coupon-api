package com.nhnacademy.bookapi.dto.book;

import lombok.Getter;

@Getter
public class BookStockRequestDTO {

    private Long bookId;
    private Integer stockToReduce;

}
