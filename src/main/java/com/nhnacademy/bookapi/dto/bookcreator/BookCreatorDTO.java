package com.nhnacademy.bookapi.dto.bookcreator;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookCreatorDTO {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String role;

    public BookCreatorDTO(String name, String role){
        this.name = name;
        this.role = role;
    }
}
