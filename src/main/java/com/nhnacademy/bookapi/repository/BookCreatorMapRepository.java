package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;

public interface BookCreatorMapRepository extends JpaRepository<BookCreatorMap, Long> {

    List<BookCreatorMap> findByBook(Book book);

    @Modifying
    void deleteAllByBookId(Long bookId);
}
