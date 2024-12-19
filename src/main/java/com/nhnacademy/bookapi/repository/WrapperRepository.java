package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Wrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WrapperRepository extends JpaRepository<Wrapper, Long> {
    boolean existsByBook(Book book);
    Optional<Wrapper> findByBook(Book book);
}
