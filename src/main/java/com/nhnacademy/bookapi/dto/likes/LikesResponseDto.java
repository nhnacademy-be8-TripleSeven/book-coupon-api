package com.nhnacademy.bookapi.dto.likes;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
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
