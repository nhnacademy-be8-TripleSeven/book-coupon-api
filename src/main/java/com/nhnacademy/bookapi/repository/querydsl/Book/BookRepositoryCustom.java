package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookOrderDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {

    BookDTO findBookById(Long id);

    Page<BookDTO> findBookByKeyword(String keyword, Pageable pageable);

    BookOrderDetailResponse findBookOrderDetail(Long id);

}
