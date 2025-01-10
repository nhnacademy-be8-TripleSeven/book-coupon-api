
package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Query("select c from Category c join BookCategory bc on bc.category.id = c.id where bc.book.id =:bookId")
    List<Category> findByBookId(@Param("bookId") Long bookId);

    Optional<Category> findCategoryByName(String name);


    Page<Category> findCategoryByLevel(int level, Pageable pageable);

    List<Category> findByNameContaining(String name);

    @Query("select c from Category c where c.level = :level")
    List<Category> findByLevel(int level);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL")
    List<Category> findAllRootCategories();

}