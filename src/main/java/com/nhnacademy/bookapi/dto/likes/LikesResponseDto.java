package com.nhnacademy.bookapi.dto.likes;

import lombok.Getter;

@Getter
public class LikesResponseDto {

    private Long bookId;

    public LikesResponseDto() {}

    public LikesResponseDto(Long bookId) {
        this.bookId = bookId;
    }

}
