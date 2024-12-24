package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookIndexRepository extends JpaRepository<BookIndex, Long> {
    boolean existsByBook(Book book);

    Optional<BookIndex> findByBook(Book book);

    List<BookIndex> findAllByBook(Book book);
}
