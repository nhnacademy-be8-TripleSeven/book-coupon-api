
package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Query("select c from Category c join BookCategory bc on bc.category.id = c.id where bc.book.id =:bookId")
    List<Category> findByBookId(@Param("bookId") Long bookId);

    Category findCategoryByName(String name);


    List<Category> findCategoryByLevel(int level);

    List<Category> findByNameContaining(String name);

}