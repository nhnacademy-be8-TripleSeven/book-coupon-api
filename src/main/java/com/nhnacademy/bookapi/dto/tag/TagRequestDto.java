package com.nhnacademy.bookapi.dto.tag;


import jakarta.validation.constraints.NotEmpty;

public class TagRequestDto {

    @NotEmpty
    private String name;

    public TagRequestDto() {}

    public TagRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(@NotEmpty String name) {
        this.name = name;
    }
}
