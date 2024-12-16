package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.entity.Book;

public interface BookService {

    Book createBook(Book book);


    Book update(Book book);

    void delete(Long id);

    SearchBookDetail searchBookDetailByBookId(Long id);

}
