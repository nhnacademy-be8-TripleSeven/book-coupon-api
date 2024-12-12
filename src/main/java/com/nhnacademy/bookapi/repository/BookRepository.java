package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.dto.book.SearchBookDTO;
import com.nhnacademy.bookapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select new SearcBookDTO(b.title, b.description, b.publishDate, b.regularPrice, b.salePrice, b.isbn13, b.stock, b.page, i.url, b.publisher.name, BookCreator) from Book b join BookImage bi on bi.book.id = b.id join Image i on i.id = bi.image.id ")
    SearchBookDTO searchBookByTitle(String title);

}


