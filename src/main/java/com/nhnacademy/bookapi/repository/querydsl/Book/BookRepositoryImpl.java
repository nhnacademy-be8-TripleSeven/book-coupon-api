package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.QBook;
import com.nhnacademy.bookapi.entity.QBookCategory;
import com.nhnacademy.bookapi.entity.QBookCoverImage;
import com.nhnacademy.bookapi.entity.QBookCreator;
import com.nhnacademy.bookapi.entity.QBookCreatorMap;
import com.nhnacademy.bookapi.entity.QBookImage;
import com.nhnacademy.bookapi.entity.QBookIndex;
import com.nhnacademy.bookapi.entity.QBookTag;
import com.nhnacademy.bookapi.entity.QBookType;
import com.nhnacademy.bookapi.entity.QCategory;
import com.nhnacademy.bookapi.entity.QImage;
import com.nhnacademy.bookapi.entity.QTag;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.entity.Type;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    public Page<BookUpdateDTO> findBookByKeyword(String keyword, Pageable pageable) {
        QBook book = QBook.book;

        // 1) 메인 쿼리
        List<BookUpdateDTO> content = from(book)
            .where(book.title.containsIgnoreCase(keyword)
                .or(book.isbn13.containsIgnoreCase(keyword)
                    .or(book.publisher.name.containsIgnoreCase(keyword))))
            .select(Projections.constructor(
                BookUpdateDTO.class,
                book.id,
                book.title,
                book.isbn13,
                book.publishDate,
                book.description,
                book.regularPrice,
                book.salePrice
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
    public void updateBook(BookUpdateDTO bookUpdateDTO) {

    }
}
