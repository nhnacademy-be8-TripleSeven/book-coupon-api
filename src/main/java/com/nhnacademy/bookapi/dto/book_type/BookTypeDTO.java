package com.nhnacademy.bookapi.dto.book_type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.bookapi.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookTypeDTO {

    private Long id;
    private int ranks;
    private String type;
    private Long bookId;

    public BookTypeDTO(Long id, int ranks, String type) {
        this.id = id;
        this.ranks = ranks;
        this.type = type;
    }

    public BookTypeDTO(String type, int ranks) {
        this.type = type;
        this.ranks = ranks;
    }



}
