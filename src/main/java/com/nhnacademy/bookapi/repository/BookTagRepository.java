package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("delete from BookTag bt where bt.book.id = :bookId")
    void deleteBookTagByBookId(Long bookId);


    @Query("SELECT COUNT(bt) > 0 FROM BookTag bt WHERE bt.book.id = :bookId")
    boolean existsByBookId(Long bookId);
    }
