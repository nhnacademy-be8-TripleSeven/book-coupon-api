package com.nhnacademy.bookapi.dto.review;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {

    private Long userId;
    private String text;
    private int rating;
    private LocalDateTime createdAt;

    public ReviewResponseDto() {}

    public ReviewResponseDto(Long userId, String text, int rating, LocalDateTime createdAt) {
        this.userId = userId;
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
    }
}
