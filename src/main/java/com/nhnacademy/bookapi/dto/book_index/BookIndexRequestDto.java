package com.nhnacademy.bookapi.dto.book_index;

import lombok.Getter;

@Getter
public class BookIndexRequestDto {

    private Long bookId;
    private String indexText;

    public BookIndexRequestDto() {
    }

    public BookIndexRequestDto(Long bookId, String indexText) {
        this.bookId = bookId;
        this.indexText = indexText;
    }

}
