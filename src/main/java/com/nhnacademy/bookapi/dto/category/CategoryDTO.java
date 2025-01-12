package com.nhnacademy.bookapi.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;
    private Integer level;
    private CategoryDTO parent;
    private Long parentCategoryId;

    public CategoryDTO(Long id, String name, Integer level, CategoryDTO parent) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.parent = parent;
    }


    public CategoryDTO(String name, Integer level, CategoryDTO parent) {
        this.name = name;
        this.level = level;
        this.parent = parent;
    }

    public CategoryDTO(Long id, String name, Integer level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public CategoryDTO(String name, Integer level) {
        this.name = name;
        this.level = level;
    }

    public CategoryDTO(String name){
        this.name = name;
    }



}
