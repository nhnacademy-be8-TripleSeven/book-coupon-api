package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookIndexRepository extends JpaRepository<BookIndex, Long> {
//    Optional<List<BookIndex>> findByBook(Book book);
//
//    void deleteByBook(Book book);
//
//    boolean existsByBookId(Long bookId);
//
//    boolean existsByBookIdAndSequence(Long bookId, int sequence);
//
//
//    Optional<BookIndex> findByBookAndSequence(Book book, int sequence);
//
//    List<BookIndex> findByBookId(Long bookId);

    /**
     * 특정 책에 연결된 모든 목차를 조회
     */
    Optional<List<BookIndex>> findByBook(Book book);

    /**
     * 특정 책에 연결된 모든 목차를 삭제
     */
    void deleteByBook(Book book);

    /**
     * 특정 책에 목차가 존재하는지 확인
     */
    boolean existsByBook(Book book);

    /**
     * 특정 책의 특정 목차를 조회
     */
    Optional<BookIndex> findByBookAndIndexText(Book book, String indexText);

}
