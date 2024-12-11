package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Wrappable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WrappableRepository extends JpaRepository<Wrappable, Long> {
    boolean existsByBook(Book book);
    Optional<Wrappable> findByBook(Book book);
}
