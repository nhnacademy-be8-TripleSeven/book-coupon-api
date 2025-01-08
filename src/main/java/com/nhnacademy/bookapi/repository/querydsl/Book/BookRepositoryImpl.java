package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.QBook;
import com.querydsl.core.types.Projections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookRepositoryImpl extends QuerydslRepositorySupport implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public BookRepositoryImpl() {
        super(Book.class);
    }

    @Transactional(readOnly = true)
    @Override
    public BookDTO findBookById(Long bookId) {
        QBook book = QBook.book;

        return from(book)
            .where(book.id.eq(bookId))
            .select(Projections.constructor(
                BookDTO.class,
                book.id,
                book.title,
                book.isbn13,
                book.publishDate,
                book.description,
                book.regularPrice,
                book.salePrice,
                book.stock,
                book.page
            ))
            .fetchOne();
    }
    @Transactional(readOnly = true)
    @Override
    public Page<BookDTO> findBookByKeyword(String keyword, Pageable pageable) {
        QBook book = QBook.book;

        // 1) 메인 쿼리
        List<BookDTO> content = from(book)
            .where(book.title.containsIgnoreCase(keyword)
                .or(book.isbn13.containsIgnoreCase(keyword)
                    .or(book.publisher.name.containsIgnoreCase(keyword))))
            .select(Projections.constructor(
                BookDTO.class,
                book.id,
                book.title,
                book.isbn13,
                book.publishDate,
                book.description,
                book.regularPrice,
                book.salePrice,
                book.stock,
                book.page
            ))
            // offset, limit 설정
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 2) 카운트 쿼리
        long total = from(book)
            .where(book.title.containsIgnoreCase(keyword)
                .or(book.isbn13.containsIgnoreCase(keyword)
                    .or(book.publisher.name.containsIgnoreCase(keyword))))
            .select(book.count())
            .fetchOne();

        // 3) Page 객체로 변환
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    @Transactional
    public void updateBook(BookDTO bookUpdateDTO) {

    }
}
