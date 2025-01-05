package com.nhnacademy.bookapi.dto.review;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {

    private Long userId;
    private String text;
    private int rating;
    private LocalDateTime createdAt;
    private String imageUrl;

    public ReviewResponseDto() {}

    public ReviewResponseDto(Long userId, String text, int rating, LocalDateTime createdAt, String imageUrl) {
        this.userId = userId;
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
    }
}
