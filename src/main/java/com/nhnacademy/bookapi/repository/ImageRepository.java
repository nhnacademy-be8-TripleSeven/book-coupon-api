package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Image;
import com.rabbitmq.client.AMQP.Confirm.Select;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select i.url from Image i join BookImage bi on bi.image.id = i.id where bi.book.id =:bookId")
    List<String> findBookImageByBookId(Long bookId);

    @Query("select i.url from Image i join BookCoverImage bci on bci.image.id = i.id where bci.book.id =:bookId")
    List<String> findBookCoverImageByBookId(Long bookId);




    @Query("select distinct i from Image i join BookImage bi on bi.image.id = i.id where bi.book.id =:bookId")
    Optional<Image> findDetailImageByBookId(Long bookId);

    @Query("select distinct i from Image i join BookCoverImage bci on bci.image.id = i.id where bci.book.id = :bookId")
    Optional<Image> findCoverImageByBookId(Long bookId);

}
