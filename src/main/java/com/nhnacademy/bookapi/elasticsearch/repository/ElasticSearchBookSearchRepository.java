package com.nhnacademy.bookapi.elasticsearch.repository;

import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchBookSearchRepository extends ElasticsearchRepository<BookDocument, String> {
    Page<BookDocument> findByTitleContaining(String title, String bookCreator, Pageable pageable);

    Page<BookDocument> findByTitleContaining(String title, Pageable pageable);

    Page<BookDocument> findByBookcreatorContaining(String bookCreator, Pageable pageable);

    Page<BookDocument> findByPublisherNameContaining(String publishName, Pageable pageable);

    Page<BookDocument> findByIsbn13(String isbn13, Pageable pageable);
}
