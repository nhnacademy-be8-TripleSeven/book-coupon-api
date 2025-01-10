package com.nhnacademy.bookapi.elasticsearch.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DocumentSearchResponseDTO {

    private String id;

    private String title;

    private String isbn13;

    private LocalDateTime publishDate;

    private int regularPrice;

    private int salePrice;

    private String coverUrl;

}
