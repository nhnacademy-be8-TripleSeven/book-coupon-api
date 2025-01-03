package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);
    void deleteByName(String name);

    Optional<Tag> findByName(String name);

    @Query("select t from Tag t join BookTag bt on bt.tag.id = t.id where bt.book.id =: bookId")
    List<Tag> findByBookId(Long bookId);
}
