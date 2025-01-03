package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookIndexRepository extends JpaRepository<BookIndex, Long> {
    boolean existsByBook(Book book);

    Optional<BookIndex> findByBook(Book book);

    List<BookIndex> findAllByBook(Book book);

    @Query("select bi.indexes from BookIndex bi where bi.book.id =:bookId")
    String findByBookId(@Param("bookId") Long bookId);


}
