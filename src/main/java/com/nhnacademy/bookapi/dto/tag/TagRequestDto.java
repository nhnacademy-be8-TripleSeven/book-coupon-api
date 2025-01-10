package com.nhnacademy.bookapi.dto.tag;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class TagRequestDto {

    @NotEmpty
    private String name;
}
