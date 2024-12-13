package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.dto.book.SearchBookDTO;
import com.nhnacademy.bookapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
}


