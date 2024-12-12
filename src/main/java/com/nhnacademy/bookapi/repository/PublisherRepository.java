package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {


    @Query("select p from Publisher p where p.name =:name")
    Publisher existsByName(String name);
}
