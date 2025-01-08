package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCoverImage;
import com.nhnacademy.bookapi.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookCoverImageRepository extends JpaRepository<BookCoverImage, Long> {

    @Query("select bci from BookCoverImage bci " +
            "join fetch bci.image " +
            "join fetch bci.book " +
            "where bci.book = :book")
    BookCoverImage findByBook(@Param("book") Book book);

    @Query("select i from Image i join BookCoverImage bci on bci.image.id = i.id where bci.book.id = :bookId")
    List<Image> findImageByBookId(@Param("bookId") Long bookId);

    @Modifying
    void deleteByBookId(Long bookId);
}
