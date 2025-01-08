package com.nhnacademy.bookapi.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategorySearchDTO {
    private Long id;
    private String name; // 카테고리명
}