package com.nhnacademy.bookapi.dto.book;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BookStockRequestDTO {

    private Long bookId;
    private Integer stockToReduce;

    @Builder
    public BookStockRequestDTO(Long bookId, Integer stockToReduce) {
        this.bookId = bookId;
        this.stockToReduce = stockToReduce;
    }

}
