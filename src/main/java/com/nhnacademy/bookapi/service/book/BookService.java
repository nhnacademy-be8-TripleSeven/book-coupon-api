package com.nhnacademy.bookapi.service.book;


import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;

import com.nhnacademy.bookapi.dto.book.*;

import com.nhnacademy.bookapi.dto.page.PageDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Type;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {



    Book createBook(Book book);

    Page<BookDTO> getBookList(String keyword, Pageable pageable);

    Book getBook(Long id);

    void deleteBook(Long id);

    SearchBookDetail searchBookDetailByBookId(Long id);

    PageDTO<BookDetailResponseDTO> getMonthlyBestBooks();

    PageDTO<BookDetailResponseDTO> getBookTypeBooks(Type bookType, Pageable pageable);



    boolean existsBookByIsbn(String isbn);

    BookDTO getBookById(Long id);

    List<BookSearchDTO> searchBooksByName(String name);



    List<OrderItemDTO> getCartItemsByIds(List<Long> bookIds);

    Page<BookDetailResponseDTO> searchBookByCategoryId(Long categoryId, Pageable pageable);

    String getBookName(Long bookId);

    void bookReduceStock(List<BookStockRequestDTO> bookStockRequestDTOList);


}
