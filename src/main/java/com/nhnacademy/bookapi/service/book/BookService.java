package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.*;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Type;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    CreateBookRequestDTO createBook(CreateBookRequestDTO createBookRequest);

    Book createBook(Book book);


    void delete(Long id);

    SearchBookDetail searchBookDetailByBookId(Long id);

    Page<BookDetailResponseDTO> getMonthlyBestBooks();

    Page<BookDetailResponseDTO> getBookTypeBooks(Type bookType, Pageable pageable);

    Page<BookDetailResponseDTO> getCategorySearchBooks(List<String> categories, String keyword, Pageable pageable);

    List<BookSearchDTO> searchBooksByName(String name);

}


