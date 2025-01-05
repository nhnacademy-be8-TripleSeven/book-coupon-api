
package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
//    Optional<Category> findByNameAndParent(String name, Category parent);

    // 특정 카테고리의 하위 카테고리 조회

//    @Query(value = """
//        WITH RECURSIVE Subcategories (ID, PARENT_ID) AS (
//            SELECT ID, PARENT_ID FROM CATEGORY WHERE ID = :categoryId
//            UNION ALL
//            SELECT c.ID, c.PARENT_ID
//            FROM CATEGORY c
//            INNER JOIN Subcategories sc ON c.PARENT_ID = sc.ID
//        )
//        SELECT ID FROM Subcategories
//    """, nativeQuery = true)
//    List<Long> findSubcategories(@Param("categoryId") Long categoryId);

    @Query("select c from Category c join BookCategory bc on bc.category.id = c.id where bc.book.id =:bookId")
    List<Category> findByBookId(@Param("bookId") Long bookId);

    Category findCategoryByName(String name);


    List<Category> findCategoryByLevel(int level);


    List<Category> findByNameContaining(String name);

}