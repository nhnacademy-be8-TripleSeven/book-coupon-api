package com.nhnacademy.bookapi.elasticsearch.repository;

import static org.apache.commons.lang3.stream.LangCollectors.collect;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.nhnacademy.bookapi.dto.book.BookSearchResponseDTO;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomBookSearchRepositoryImpl implements CustomBookSearchRepository {

    private ElasticsearchTemplate elasticsearchTemplate;

    private Page<BookSearchResponseDTO> mapToDTOPage(List<BookDocument> documents, Pageable pageable, long totalHits) {
        List<BookSearchResponseDTO> dtos = documents.stream()
            .map(doc -> new BookSearchResponseDTO(
                doc.getId(),
                doc.getIsbn13(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getPublishDate(),
                doc.getRegularPrice(),
                doc.getSalePrice(),
                doc.getStock(),
                doc.getPage(),
                doc.getBestSellerRank(),
                doc.getClickCount(),
                doc.getSearchCount(),
                doc.getCartCount(),
                doc.getCoverUrl(),
                doc.getPublisherName(),
                doc.getBookCreators(),
                doc.getCategories()
            ))
            .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, totalHits);
    }


}
