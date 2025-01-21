package com.nhnacademy.bookapi.elasticsearch.repository;

import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

public interface ElasticSearchBookSearchRepository extends ElasticsearchRepository<BookDocument, String>, CustomBookSearchRepository {
    @Query("{ " +
        "  \"function_score\": { " +
        "    \"query\": { " +
        "      \"bool\": { " +
        "        \"should\": [ " +
        "          { \"match\": { \"title\":         { \"query\": \"?0\", \"boost\": 4, \"fuzziness\": \"AUTO\" } } }, " +
        "          { \"match\": { \"isbn13\":        { \"query\": \"?0\", \"boost\": 1 } } }, " +
        "          { \"match\": { \"bookcreators\":  { \"query\": \"?0\", \"boost\": 3 } } }, " +
        "          { \"match\": { \"publishername\": { \"query\": \"?0\", \"boost\": 2 } } }, " +
        "          { \"match\": { \"title_split\":   { \"query\": \"?0\", \"boost\": 0.5 } } } " +
        "        ] " +
        "      } " +
        "    }, " +
        "    \"boost_mode\": \"sum\" " +
        "  } " +
        "}")
    Page<BookDocument> getBookDocumentByKeyword(String keyword, Pageable pageable);


}
