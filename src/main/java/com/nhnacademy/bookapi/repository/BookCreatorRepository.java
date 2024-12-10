package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookCreator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCreatorRepository extends JpaRepository<BookCreator, Long> {

}
