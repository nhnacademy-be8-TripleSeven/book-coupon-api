package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {

    BookDTO findBookById(Long id);

    Page<BookDTO> findBookByKeyword(String keyword, Pageable pageable);

    void updateBook(BookDTO bookUpdateDTO);

}
