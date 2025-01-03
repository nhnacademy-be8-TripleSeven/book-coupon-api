package com.nhnacademy.bookapi.dto.bookcreator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCreatorDTO {

    private Long id;
    private String name;
    private String role;

    public BookCreatorDTO(String name, String role){
        this.name = name;
        this.role = role;
    }
}
