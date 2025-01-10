package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookTypeRepository extends JpaRepository<BookType, Long> {

    List<BookType> findAllByBook(Book book);

    @Query("select bt from BookType bt where bt.book.id = :bookId")
    List<BookType> findByBookId(@Param("bookId") Long bookId);

    @Modifying
    void deleteAllByBookId(Long bookId);
}
