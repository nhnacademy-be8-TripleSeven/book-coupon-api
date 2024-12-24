package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Type;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select new com.nhnacademy.bookapi.dto.book.SearchBookDetail(b.title, b.description, b.publishDate, b.regularPrice, b.salePrice, b.isbn13, b.stock, b.page, i.url, b.publisher.name) from Book b inner join BookImage bi on bi.book.id = b.id inner join Image i on i.id = bi.image.id where b.id =:id")
    Optional<SearchBookDetail> searchBookById(@Param("id") Long id);

    boolean existsByIsbn13(String isbn13);

    // 이달의 베스트
    @Query("select new com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO(b.id, b.title, b.publisher.name, b.regularPrice, b.salePrice, i.url) "
            + "from Book b "
            + "join BookType bt on bt.book.id = b.id "
            + "join BookCoverImage bci on bci.book.id = b.id "
            + "join Image i on i.id = bci.image.id "
            + "where bt.types = 'BESTSELLER' "
            + "order by bt.ranks asc")
    Page<BookDetailResponseDTO> findBookTypeBestsellerByRankAsc(Pageable pageable);


    //이 도서는 어때요? ITEMNEWSPECIAL
    @Query("select new com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO(b.id, b.title, b.publisher.name, b.regularPrice, b.salePrice, i.url) " +
            "from Book b " +
            "join BookType bt on bt.book.id = b.id " +
            "join BookCoverImage bci on bci.book.id = b.id " +
            "join Image i on i.id = bci.image.id " +
            "order by b.title asc")
    List<BookDetailResponseDTO> findBookTypeItemNewSpecial();

    //북 타입별 조회
    @Query("select new com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO(b.id, b.title, b.publisher.name, b.regularPrice, b.salePrice, i.url) "
            + "from Book b "
            + "join BookType bt on bt.book.id = b.id "
            + "join BookCoverImage bci on bci.book.id = b.id "
            + "join Image i on i.id = bci.image.id "
            + "where bt.types =:type")
    Page<BookDetailResponseDTO> findBookTypeItemByType(@Param("type") Type type, Pageable pageable);

    @EntityGraph(attributePaths = {"publisher"})
    @Query("select b from Book b where b.id= :id")
    Optional<Book> findBookWithPublisherById(Long id);


}


