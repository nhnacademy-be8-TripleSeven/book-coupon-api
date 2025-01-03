package com.nhnacademy.bookapi.dto.likes;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LikesResponseDto {

    private Long bookId;
    private String bookTitle;
    private LocalDateTime createdAt;

    public LikesResponseDto() {}

    public LikesResponseDto(Long bookId, String bookTitle, LocalDateTime createdAt) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.createdAt = createdAt;
    }

}
