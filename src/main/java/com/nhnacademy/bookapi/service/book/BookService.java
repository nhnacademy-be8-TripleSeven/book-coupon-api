package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookSearchResponseDTO;
import com.nhnacademy.bookapi.dto.book.CreateBookRequestDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.book.UpdateBookRequest;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    CreateBookRequestDTO createBook(CreateBookRequestDTO createBookRequest);

    Book createBook(Book book);


    UpdateBookRequest update(UpdateBookRequest request);

    void delete(Long id);

    SearchBookDetail searchBookDetailByBookId(Long id);

    Page<BookDetailResponseDTO> getMonthlyBestBooks();

    Page<BookDetailResponseDTO> getBookTypeBooks(Type bookType, Pageable pageable);

}
