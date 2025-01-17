package com.nhnacademy.bookapi.dto.book;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class BookDetailResponseDTO {

    @NotBlank
    private long id;

    @NotBlank(message = "제목은 반드시 입력해야 합니다.")
    private String title;

    @NotBlank(message = "출판사는 반드시 입력해야 합니다.")
    private String publisher;

    @Min(value = 0, message = "정가(regularPrice)는 0 이상이어야 합니다.")
    private int regularPrice;

    @Min(value = 0, message = "판매가(salePrice)는 0 이상이어야 합니다.")
    private int salePrice;

    @NotBlank(message = "커버는 반드시 있어야 합니다.")
    private String coverUrl;

    @Setter
    private List<String> creator;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    public BookDetailResponseDTO(Long id, String title, String publisher, int regularPrice, int salePrice,
        String coverUrl, LocalDate publishDate) {
        this.id = id;
        this.title = title;
        this.publisher = publisher;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.coverUrl = coverUrl;
        this.publishDate = publishDate;
    }
}
