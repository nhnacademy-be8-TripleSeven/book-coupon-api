package com.nhnacademy.bookapi.service.book_type;

import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Type;
import java.util.List;

public interface BookTypeService {
    List<BookTypeDTO> getUpdateBookTypeList(long bookId);

    List<BookType> getBookTypeByBookId(long bookId);

    void deleteBookType(long bookId);

    void createBookType(BookType bookType);
}
