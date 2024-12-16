package com.nhnacademy.bookapi.dto.likes;

public class LikesRequestDto {

    private Long bookId;
    private Long userId;

    public LikesRequestDto() {}

    public LikesRequestDto(Long bookId, Long userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
