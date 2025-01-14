package com.nhnacademy.bookapi.elasticsearch.service;


import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.dto.DocumentSearchResponseDTO;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookSearchServiceTest {

    @InjectMocks
    private BookSearchService bookSearchService;

    @Mock
    private ElasticSearchBookSearchRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testElasticSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDocument> mockBookDocuments = List.of(
            BookDocument.builder().title("Test Title")
                .isbn13("1234567890123").coverUrl("http://cover.url").regularPrice(1000).salePrice(1000)
                .build()
        );

        PageImpl page = new PageImpl(mockBookDocuments, pageable, mockBookDocuments.size());
        when(repository.searchWithPopularityAndWeights(anyString(), eq(pageable))).thenReturn(page);

        // Act
        Page<DocumentSearchResponseDTO> result = bookSearchService.elasticSearch("test", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Title");
        assertThat(result.getContent().get(0).getIsbn13()).isEqualTo("1234567890123");
        assertThat(result.getContent().get(0).getCoverUrl()).isEqualTo("http://cover.url");

        verify(repository, times(1)).searchWithPopularityAndWeights(eq("test"), eq(pageable));
    }

    @Test
    void testElasticSearchWithEmptyResult() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.searchWithPopularityAndWeights(anyString(), eq(pageable))).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        // Act
        Page<DocumentSearchResponseDTO> result = bookSearchService.elasticSearch("noresult", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(repository, times(1)).searchWithPopularityAndWeights(eq("noresult"), eq(pageable));
    }
}
