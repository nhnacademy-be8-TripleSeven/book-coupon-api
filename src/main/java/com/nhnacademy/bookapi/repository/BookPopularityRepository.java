package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookPopularity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookPopularityRepository extends JpaRepository<BookPopularity, Long> {

}
