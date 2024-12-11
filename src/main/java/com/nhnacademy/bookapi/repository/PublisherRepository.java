package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

}
