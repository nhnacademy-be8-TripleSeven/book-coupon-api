package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);
    void deleteByName(String name);

    Optional<Tag> findByName(String name);
}
