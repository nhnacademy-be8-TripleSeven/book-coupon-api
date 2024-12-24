package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {

    Optional<BookImage> findFirstByBookOrderByIdAsc(Book book);
}
