package com.nhnacademy.bookapi.dto.book_index;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class BookIndexResponseDto {

    private String indexText;

    public BookIndexResponseDto(String indexText) {
        this.indexText = indexText;
    }
}
