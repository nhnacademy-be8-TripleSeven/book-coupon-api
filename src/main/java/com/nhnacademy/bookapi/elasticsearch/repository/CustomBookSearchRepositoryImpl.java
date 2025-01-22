package com.nhnacademy.bookapi.elasticsearch.repository;


import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery.Builder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.dto.BookPopularityDTO;
import com.nhnacademy.bookapi.exception.ElasticPopularityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

@RequiredArgsConstructor
public class CustomBookSearchRepositoryImpl implements CustomBookSearchRepository {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ElasticsearchOperations operations;


    @Override
    public List<BookPopularityDTO> getAllBookPopularities() {
        MatchAllQuery matchAll = new Builder().build();
        Query esQuery = new Query.Builder().matchAll(matchAll).build();
        NativeQuery nativeQuery = new NativeQueryBuilder()
            .withQuery(esQuery)
            .withFields("id", "_score")
            .build();

        try {
            SearchHits<BookDocument> search = elasticsearchTemplate.search(nativeQuery, BookDocument.class);

            return search.stream()
                .map(hit -> {
                    BookDocument bookDocument = hit.getContent();
                    return new BookPopularityDTO(bookDocument.getId(), bookDocument.getPopularity());
                }).toList();
        }catch (Exception e){
            throw new ElasticPopularityException("검색횟수 업데이트가 실패했습니다.");
        }
    }


}
