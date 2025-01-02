package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.*;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    CreateBookRequestDTO createBook(CreateBookRequestDTO createBookRequest);

    Book createBook(Book book);


    UpdateBookRequest update(UpdateBookRequest request);

    void delete(Long id);

    SearchBookDetail searchBookDetailByBookId(Long id);

    Page<BookDetailResponseDTO> getMonthlyBestBooks();

    Page<BookDetailResponseDTO> getBookTypeBooks(Type bookType, Pageable pageable);

    List<BookSearchDTO> searchBooksByName(String name);
}
