package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {


    List<Category> findCategoryByBook(Book book);

    @Query("select bc from BookCategory bc join fetch bc.category where bc.book = :book")
    List<BookCategory> findAllByBook(Book book);

    @Modifying
    void deleteAllByBookId(Long bookId);

    /**
     * 특정 책(Book)과 연관된 모든 카테고리를 반환합니다.
     */
    @Query("SELECT bc.category FROM BookCategory bc WHERE bc.book.id = :bookId")
    List<Category> findCategoriesByBookId(@Param("bookId") Long bookId);
}
