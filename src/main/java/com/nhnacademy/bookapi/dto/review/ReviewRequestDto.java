package com.nhnacademy.bookapi.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewRequestDto {

    @NotBlank(message = "리뷰 내용은 비어있을 수 없습니다.")
    private String text;
    @NotNull(message = "평점은 필수입니다.")
    private int rating;
    private Long bookId;
    private String imageUrl;

    public ReviewRequestDto(String text, int rating, Long bookId, String imageUrl) {
        this.text = text;
        this.rating = rating;
        this.bookId = bookId;
        this.imageUrl = imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public Long getBookId() {
        return bookId;
    }
}
