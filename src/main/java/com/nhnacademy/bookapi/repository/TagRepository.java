package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
