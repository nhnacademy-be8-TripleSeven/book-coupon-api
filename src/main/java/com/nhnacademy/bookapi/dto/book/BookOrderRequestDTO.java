package com.nhnacademy.bookapi.dto.book;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookOrderRequestDTO {

    @NotNull
    private long bookId;
    @NotNull
    private int quantity;

}
