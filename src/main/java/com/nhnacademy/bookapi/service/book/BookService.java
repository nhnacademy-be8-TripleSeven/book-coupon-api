package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.CreateBookRequestDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Type;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    CreateBookRequestDTO createBook(CreateBookRequestDTO createBookRequest);

    Book createBook(Book book);

    Page<BookDTO> getBookList(String keyword, Pageable pageable);

    Book getBook(Long id);

    void deleteBook(Long id);

    SearchBookDetail searchBookDetailByBookId(Long id);

    Page<BookDetailResponseDTO> getMonthlyBestBooks();

    Page<BookDetailResponseDTO> getBookTypeBooks(Type bookType, Pageable pageable);

    Page<BookDetailResponseDTO> getCategorySearchBooks(List<String> categories, String keyword, Pageable pageable);

    boolean existsBookByIsbn(String isbn);

    BookDTO getBookById(Long id);


}
