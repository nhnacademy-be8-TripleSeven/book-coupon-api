package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {

}
