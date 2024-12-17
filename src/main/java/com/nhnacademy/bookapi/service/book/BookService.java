package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.CreateBookRequest;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.book.UpdateBookRequest;
import com.nhnacademy.bookapi.entity.Book;

public interface BookService {

    CreateBookRequest createBook(CreateBookRequest createBookRequest);

    Book createBook(Book book);


    UpdateBookRequest update(UpdateBookRequest request);

    void delete(Long id);

    SearchBookDetail searchBookDetailByBookId(Long id);

}
