package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookPopularity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookPopularityRepository extends JpaRepository<BookPopularity, Long> {

    @Modifying
    @Query("delete from BookPopularity bp where bp.book.id =:bookId")
    void deleteByBookId(Long bookId);
}
