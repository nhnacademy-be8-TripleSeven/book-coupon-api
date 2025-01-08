package com.nhnacademy.bookapi.dto.book_index;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookIndexResponseDto {

    private Long id;

    private String indexText;

    private long bookId;

    public BookIndexResponseDto(String indexText) {
        this.indexText = indexText;
    }
}
