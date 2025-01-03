package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {

    Optional<BookImage> findFirstByBookOrderByIdAsc(Book book);

    @Query("select bi from BookImage bi join fetch bi.image where bi.book = :book")
    List<BookImage> findAllByBookWithImage(@Param("book") Book book);



}
