package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByName(String name);

    @Query("select t.name from Tag t join BookTag bt on bt.tag.id = t.id where bt.book.id =:bookId")
    List<String> findTagNameByBookId(Long bookId);
}
