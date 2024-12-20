package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select new com.nhnacademy.bookapi.dto.book.SearchBookDetail(b.title, b.description, b.publishDate, b.regularPrice, b.salePrice, b.isbn13, b.stock, b.page, i.url, b.publisher.name) from Book b inner join BookImage bi on bi.book.id = b.id inner join Image i on i.id = bi.image.id where b.id =:id")
    Optional<SearchBookDetail> searchBookById(@Param("id") Long id);

    boolean existsByIsbn13(String isbn13);


    // 이달의 베스트
    @Query("select b.title, bt.ranks from Book b join BookType bt on bt.book.id = b.id order by bt.ranks asc limit 3")
    List<Book> findBookTypeBestsellerByRankAsc();


    //이 도서는 어때요? ITEMNEWSPECIAL
    @Query("select b.title,b.salePrice , bc.name, bc.role " +
        "from Book b " +
        "join BookType bt on bt.book.id = b.id " +
        "join BookCreatorMap bcm on bcm.book.id = b.id " +
        "join BookCreator bc on bc.id = bcm.creator.id " +
        "order by b.title asc limit 8")
    List<Book> findBookTypeItemNewSpecial();
}


