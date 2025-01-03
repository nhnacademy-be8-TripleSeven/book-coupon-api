package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Image;
import com.rabbitmq.client.AMQP.Confirm.Select;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select i from Image i join BookImage bi on bi.image.id = i.id where bi.book =: bookId")
    List<Image> findBookImageByBookId(Long bookId);

    @Query("select i from Image i join BookCoverImage bci on bci.image.id = i.id where bci.book =: bookId")
    List<Image> findBookCoverImageByBookId(Long bookId);
}
