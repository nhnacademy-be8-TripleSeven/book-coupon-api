package com.nhnacademy.bookapi.dto.category;

import lombok.Getter;

@Getter
public class CategoryDTO {

    private long id;
    private String name;
    private long categoryId;
    public CategoryDTO(long id, String name, long categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }
}
