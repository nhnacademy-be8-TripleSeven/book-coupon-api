package com.nhnacademy.bookapi.dto.book_tag;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class BookTagResponseDTO {
//
    private Long bookId;
    private Long tagId;
    private String tagName;

    public BookTagResponseDTO(Long bookId, Long tagId, String tagName) {
        this.bookId = bookId;
        this.tagId = tagId;
        this.tagName = tagName;
    }
}
