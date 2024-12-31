package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl extends QuerydslRepositorySupport implements BookRepositoryCustom {


    public BookRepositoryImpl() {
        super(Book.class);
    }


    @Override
    public List<BookUpdateDTO> findBookByKeyword(String keyword) {

        return null;
    }

    @Override
    public BookUpdateDTO updateBook(BookUpdateDTO bookUpdateDTO) {
        return null;
    }
}
