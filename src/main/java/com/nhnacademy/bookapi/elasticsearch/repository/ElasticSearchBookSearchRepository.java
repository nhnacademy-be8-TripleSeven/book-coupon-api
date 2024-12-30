package com.nhnacademy.bookapi.elasticsearch.repository;

import com.nhnacademy.bookapi.dto.book.BookSearchResponseDTO;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.entity.Book;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchBookSearchRepository extends ElasticsearchRepository<BookDocument, String>, CustomBookSearchRepository {


    Page<BookSearchResponseDTO> findByTitleContaining(String title, String bookCreator, Pageable pageable);

    // 카테고리 검색
    @Query("{\"bool\": {\"should\": [" +
        "{\"terms\": {\"categories\": ?0}}," +
        "{\"match\": {\"title\": \"?1\"}}" +
        "]}}")
    Page<BookDocument> findByCategoriesContainingOrTitleContainingOrderByPublishDateDesc(
        List<String> categories,
        String keyword,
        Pageable pageable
    );

    // 출판일 기준 정렬
    Page<BookDocument> findByTitleContaining(
        String title,
        Pageable pageable
    );
}
