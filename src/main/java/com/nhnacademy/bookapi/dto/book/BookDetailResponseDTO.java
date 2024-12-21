package com.nhnacademy.bookapi.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookDetailResponseDTO {


    @NotBlank(message = "제목은 반드시 입력해야 합니다.")
    private String title;

    @NotBlank(message = "출판사는 반드시 입력해야 합니다.")
    private String publisher;

    @Min(value = 0, message = "정가(regularPrice)는 0 이상이어야 합니다.")
    private int regularPrice;

    @Min(value = 0, message = "판매가(salePrice)는 0 이상이어야 합니다.")
    private int salePrice;

}
