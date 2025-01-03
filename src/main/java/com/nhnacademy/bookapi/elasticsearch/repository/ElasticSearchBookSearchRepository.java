package com.nhnacademy.bookapi.elasticsearch.repository;

import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchBookSearchRepository extends ElasticsearchRepository<BookDocument, String>, CustomBookSearchRepository {


    @Query("{\"bool\": {\"should\": [ " +
        "{\"terms\": {\"categories\": ?0}}, " +
        "{\"match\": {\"title\": ?1}}" +
        "], " +
        "\"must\": {\"exists\": {\"field\": \"publishDate\"}}" +
        "}}")
    Page<BookDocument> searchByCategoriesOrTitle(List<String> categories, String keyword, Pageable pageable);

    // 출판일 기준 정렬
    @Query("{ " +
        "  \"function_score\": { " +
        "    \"query\": { " +
        "      \"bool\": { " +
        "        \"should\": [ " +
        "          { \"match\": { \"title\": { \"query\": \"?0\", \"boost\": 4 } } }, " +
        "          { \"match\": { \"isbn13\": { \"query\": \"?0\", \"boost\": 1 } } }, " +
        "          { \"match\": { \"bookcreators\": { \"query\": \"?0\", \"boost\": 3 } } }, " +
        "          { \"match\": { \"publishername\": { \"query\": \"?0\", \"boost\": 2 } } } " +
        "        ] " +
        "      } " +
        "    }, " +
        "    \"boost_mode\": \"sum\", " +
        "    \"script_score\": { " +
        "      \"script\": { " +
        "        \"source\": \"_score + doc['popularity'].value\" " +
        "      } " +
        "    } " +
        "  } " +
        "}")
    Page<BookDocument> searchWithPopularityAndWeights(String keyword, Pageable pageable);

}
