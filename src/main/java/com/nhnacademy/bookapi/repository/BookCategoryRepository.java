package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

}
