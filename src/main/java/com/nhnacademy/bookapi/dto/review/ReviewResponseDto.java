package com.nhnacademy.bookapi.dto.review;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {

    private String text;
    private int rating;
    private LocalDateTime createdAt;

    public ReviewResponseDto() {}

    public ReviewResponseDto(String text, int rating, LocalDateTime createdAt) {
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
    }
}
