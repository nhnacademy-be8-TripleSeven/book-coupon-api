package com.nhnacademy.bookapi.dto.book_type;

import com.nhnacademy.bookapi.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookTypeDTO {

    private long id;
    private int ranks;
    private Type type;
    private long bookId;

}
