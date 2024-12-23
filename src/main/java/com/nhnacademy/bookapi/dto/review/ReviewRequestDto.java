package com.nhnacademy.bookapi.dto.review;

public class ReviewRequestDto {

    private String text;
    private int rating;
    private Long bookId;

    public ReviewRequestDto(String text, int rating, Long bookId) {
        this.text = text;
        this.rating = rating;
        this.bookId = bookId;
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
