package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookIndexRepository extends JpaRepository<BookIndex, Long> {
    Optional<List<BookIndex>> findByBook(Book book);
    void deleteByBook(Book book);

    boolean existsByBookAndSequence(Book book, int sequence);

    Optional<BookIndex> findByBookAndSequence(Book book, int sequence);

    List<BookIndex> findByBookId(Long id);
}
