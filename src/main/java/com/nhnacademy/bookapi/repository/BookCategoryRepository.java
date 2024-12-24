package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {


    List<Category> findCategoryByBook(Book book);

    @Query("select bc from BookCategory bc join fetch bc.category where bc.book = :book")
    List<BookCategory> findAllByBook(Book book);
}
