package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import java.util.List;

public interface BookRepositoryCustom {


    List<BookUpdateDTO> findBookByKeyword(String keyword);

    BookUpdateDTO updateBook(BookUpdateDTO bookUpdateDTO);
}
