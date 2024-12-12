package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO;

public interface BookService {

    SearchBookDetailDTO searchBookDetailByBookId(Long id);

}
