package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCreatorMapRepository extends JpaRepository<BookCreatorMap, Long> {

    List<BookCreatorMap> findByBook(Book book);
}
