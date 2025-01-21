package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookPopularity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookPopularityRepository extends JpaRepository<BookPopularity, Long> {

    @Modifying
    @Query("delete from BookPopularity bp where bp.book.id =:bookId")
    void deleteByBookId(Long bookId);

    boolean existsByBookId(Long bookId);

    @Query("select bp from BookPopularity bp join fetch bp.book where bp.bookId = :bookId")
    Optional<BookPopularity> findByBookId(Long bookId);
}
