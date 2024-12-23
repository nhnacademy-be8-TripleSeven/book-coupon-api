package com.nhnacademy.bookapi.dto.book_index;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class BookIndexResponseDto {

    private String indexText;
    private int sequence;

    public BookIndexResponseDto(String indexText, int sequence) {
        this.indexText = indexText;
        this.sequence = sequence;
    }
}
