package com.nhnacademy.bookapi.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    @NotBlank(message = "리뷰 내용은 비어있을 수 없습니다.")
    private String text;
    @NotNull(message = "평점은 필수입니다.")
    private int rating;
    private Long bookId;
    //private MultipartFile image;

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

}
