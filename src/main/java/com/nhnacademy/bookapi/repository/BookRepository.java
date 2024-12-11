package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}