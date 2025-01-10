package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Image;
import com.rabbitmq.client.AMQP.Confirm.Select;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select i.url from Image i join BookImage bi on bi.image.id = i.id where bi.book.id =:bookId")
    List<String> findBookImageByBookId(Long bookId);

    @Query("select i.url from Image i join BookCoverImage bci on bci.image.id = i.id where bci.book.id =:bookId")
    List<String> findBookCoverImageByBookId(Long bookId);

    @Query("select i from Image i join BookCoverImage bci on bci.image.id = i.id where bci.book.id = :bookId")
    List<Image> findCoverImageByBookId(Long bookId);

    @Query("select i from Image i join BookImage bci on bci.image.id = i.id where bci.book.id = :bookId")
    List<Image> findDetailImageByBookId(Long bookId);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.url = :path")
    void deleteByUrl(String path);
}
