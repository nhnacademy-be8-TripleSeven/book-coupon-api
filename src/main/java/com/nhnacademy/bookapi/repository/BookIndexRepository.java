package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookIndexRepository extends JpaRepository<BookIndex, Long> {

    List<BookIndex> findByBook(Book book);
    void deleteByBook(Book book);

    List<BookIndex> findByBookId(Long bookId);
}
