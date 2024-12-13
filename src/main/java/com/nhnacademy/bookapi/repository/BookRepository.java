package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO;
import com.nhnacademy.bookapi.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {


    @Query("select new com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO(b.title, b.description, b.publishDate, b.regularPrice, b.salePrice, b.isbn13, b.stock, b.page, i.url, b.publisher.name) from Book b inner join BookImage bi on bi.book.id = b.id inner join Image i on i.id = bi.image.id where b.id =:id")
    Optional<SearchBookDetailDTO> searchBookById(@Param("id") Long id);


}


