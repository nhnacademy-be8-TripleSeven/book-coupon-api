package com.nhnacademy.bookapi.elasticsearch.service;


import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import com.nhnacademy.bookapi.dto.book.BookSearchResponseDTO;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.dto.BookPopularityDTO;
import com.nhnacademy.bookapi.elasticsearch.dto.DocumentSearchResponseDTO;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import com.nhnacademy.bookapi.service.book.BookMultiTableService;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchService {


    private final ElasticSearchBookSearchRepository repository;
    private final BookMultiTableService bookMultiTableService;

    public Page<DocumentSearchResponseDTO> searchBook(String keyword, Pageable pageable) {
        Page<BookDocument> bookDocumentByKeyword = repository.getBookDocumentByKeyword(keyword,
            pageable);


        List<DocumentSearchResponseDTO> documentSearchResponseDTOS = bookDocumentByKeyword.stream()
            .map(bookDocument -> new DocumentSearchResponseDTO(
                bookDocument.getId(),
                bookDocument.getTitle(),
                bookDocument.getIsbn13(),
                bookDocument.getPublishDate(),
                bookDocument.getRegularPrice(),
                bookDocument.getSalePrice(),
                bookDocument.getCoverUrl()
            )).toList();


        return new PageImpl<>(documentSearchResponseDTOS, pageable, bookDocumentByKeyword.getTotalElements());
    }


    public void bookPopularityDbUpdate(){
        List<BookPopularityDTO> allBookPopularities = repository.getAllBookPopularities();
        for (BookPopularityDTO allBookPopularity : allBookPopularities) {
            try {
                bookMultiTableService.updateSearchRank(Long.parseLong(allBookPopularity.getId()),
                    allBookPopularity.getPopularity());
            }catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
    }




}
