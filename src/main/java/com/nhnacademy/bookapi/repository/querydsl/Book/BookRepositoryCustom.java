package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {


    Page<BookUpdateDTO> findBookByKeyword(String keyword, Pageable pageable);

    void updateBook(BookUpdateDTO bookUpdateDTO);
}
