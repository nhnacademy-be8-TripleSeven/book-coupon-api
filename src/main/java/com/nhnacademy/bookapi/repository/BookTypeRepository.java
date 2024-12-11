package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTypeRepository extends JpaRepository<BookType, Long> {

}
