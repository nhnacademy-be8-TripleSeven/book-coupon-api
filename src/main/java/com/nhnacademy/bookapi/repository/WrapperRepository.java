package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Wrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WrapperRepository extends JpaRepository<Wrapper, Long> {
    boolean existsByBook(Book book);
    Optional<Wrapper> findByBook(Book book);


    @Modifying
    @Query("delete from Wrapper w where w.book.id =:bookId")
    void deleteByBookId(Long bookId);

    boolean existsByBookId(Long bookId);
}
