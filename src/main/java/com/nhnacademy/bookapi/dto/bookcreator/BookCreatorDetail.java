package com.nhnacademy.bookapi.dto.bookcreator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCreatorDetail {

    private String name;
    private String role;

    public BookCreatorDetail(String name, String role) {
        this.name = name;
        this.role = role;
    }
}
