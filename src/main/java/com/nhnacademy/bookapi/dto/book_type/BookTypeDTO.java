package com.nhnacademy.bookapi.dto.book_type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.bookapi.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookTypeDTO {

    private Long id;
    private int ranks;
    @JsonProperty("type")
    private Type type;
    private Long bookId;

    public BookTypeDTO(Long id, int ranks, Type type) {
        this.id = id;
        this.ranks = ranks;
        this.type = type;
    }

    public BookTypeDTO(int ranks, Type type) {
        this.ranks = ranks;
        this.type = type;
    }
}
