package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO;
import com.nhnacademy.bookapi.entity.Book;

public interface BookService {

    Book save(Book book);


    Book update(Book book);

    void delete(Long id);

    SearchBookDetailDTO searchBookDetailByBookId(Long id);

}
