package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface BookTypeRepository extends JpaRepository<BookType, Long> {

    List<BookType> findAllByBook(Book book);

    @Query("select bt from BookType bt where bt.book.id =:bookId")
    List<BookType> findByBookId(Long bookId);
}
