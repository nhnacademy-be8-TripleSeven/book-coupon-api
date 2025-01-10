package com.nhnacademy.bookapi.elasticsearch.service;


import com.nhnacademy.bookapi.elasticsearch.dto.DocumentSearchResponseDTO;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookSearchService {


    private final ElasticSearchBookSearchRepository repository;


    public Page<DocumentSearchResponseDTO> elasticSearch(String keyword, Pageable pageable) {
        List<DocumentSearchResponseDTO> list = repository.searchWithPopularityAndWeights(keyword, pageable)
            .stream().map(
                bookDocument -> new DocumentSearchResponseDTO(
                    bookDocument.getId(),
                    bookDocument.getTitle(),
                    bookDocument.getIsbn13(),
                    bookDocument.getPublishDate(),
                    bookDocument.getRegularPrice(),
                    bookDocument.getSalePrice(),
                    bookDocument.getCoverUrl())).toList();
        return new PageImpl<>(list, pageable, list.size());
    }
}
