package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpPlus;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {

    List<BookTag> findByBook(Book book);
    boolean existsByBookAndTag(Book book, Tag tag);
    boolean existsByBook(Book book);
    Optional<BookTag> findByBookAndTag(Book book, Tag tag);
}
