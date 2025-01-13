package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookOrderDetailResponse;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.QBook;
import com.nhnacademy.bookapi.entity.QBookCategory;
import com.nhnacademy.bookapi.entity.QBookCoverImage;
import com.nhnacademy.bookapi.entity.QBookCreator;
import com.nhnacademy.bookapi.entity.QBookCreatorMap;
import com.nhnacademy.bookapi.entity.QCategory;
import com.nhnacademy.bookapi.entity.QImage;
import com.nhnacademy.bookapi.entity.QWrapper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
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
    public BookOrderDetailResponse findBookOrderDetail(Long id) {
        QBook book = QBook.book;
        QWrapper wrapper = QWrapper.wrapper;
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QCategory category = QCategory.category;
        QImage image = QImage.image;
        QBookCoverImage coverImage = QBookCoverImage.bookCoverImage;
        QBookCreatorMap bookCreatorMap = QBookCreatorMap.bookCreatorMap;
        QBookCreator bookCreator = QBookCreator.bookCreator;

        // 메인 DTO 생성
        return from(book)
            .leftJoin(coverImage).on(coverImage.book.id.eq(book.id))
            .leftJoin(image).on(coverImage.image.id.eq(image.id))
            .leftJoin(wrapper).on(wrapper.book.id.eq(book.id))
            .where(book.id.eq(id))
            .select(Projections.constructor(
                BookOrderDetailResponse.class,
                book.id,
                book.title,
                book.regularPrice,
                book.salePrice,
                image.url.as("coverUrl"),
                wrapper.wrappable))
            .fetchOne();
    }

}
