package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.expression.spel.ast.OpPlus;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {

    boolean existsByBookAndTag(Book book, Tag tag);
    Optional<BookTag> findByBookAndTag(Book book, Tag tag);
    @Query("SELECT bt FROM BookTag bt JOIN FETCH bt.tag WHERE bt.book = :book")
    List<BookTag> findAllByBookWithTags(Book book);
}
