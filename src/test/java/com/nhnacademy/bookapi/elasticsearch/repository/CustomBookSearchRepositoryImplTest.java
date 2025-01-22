package com.nhnacademy.bookapi.elasticsearch.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery.Builder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.dto.BookPopularityDTO;
import com.nhnacademy.bookapi.exception.ElasticPopularityException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

class CustomBookSearchRepositoryImplTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private ElasticsearchTemplate elasticsearchTemplate;

    @InjectMocks
    private CustomBookSearchRepositoryImpl customBookSearchRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetAllBookPopularities() {
        // Arrange
        MatchAllQuery matchAll = new MatchAllQuery.Builder().build();
        Query esQuery = new Query.Builder().matchAll(matchAll).build();
        NativeQuery nativeQuery = new NativeQueryBuilder()
            .withQuery(esQuery)
            .withFields("id", "_score")
            .build();

        SearchHits<BookDocument> mockSearchHits = mock(SearchHits.class);
        BookDocument bookDocument1 = BookDocument.builder().id("1").popularity(100L).build();
        BookDocument bookDocument2 = BookDocument.builder().id("2").popularity(200L).build();

        SearchHit searchHit1 = mock(SearchHit.class);
        SearchHit searchHit2 = mock(SearchHit.class);

        when(searchHit1.getContent()).thenReturn(bookDocument1);
        when(searchHit2.getContent()).thenReturn(bookDocument2);

        when(elasticsearchTemplate.search(nativeQuery, BookDocument.class)).thenReturn(mockSearchHits);

        // Act

        ElasticPopularityException exception = assertThrows(
            ElasticPopularityException.class,
            () -> customBookSearchRepository.getAllBookPopularities());

        assertEquals("검색횟수 업데이트가 실패했습니다.", exception.getMessage());

    }


}
