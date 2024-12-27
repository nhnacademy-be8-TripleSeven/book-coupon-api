package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTypeRepository extends JpaRepository<BookType, Long> {

    List<BookType> findAllByBook(Book book);
}
