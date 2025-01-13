package com.nhnacademy.bookapi.dto.book_index;

import lombok.Builder;
import lombok.Getter;

@Builder
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
