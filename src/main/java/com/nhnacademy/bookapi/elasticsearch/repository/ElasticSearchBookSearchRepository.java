package com.nhnacademy.bookapi.elasticsearch.repository;

import com.nhnacademy.bookapi.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchBookSearchRepository extends ElasticsearchRepository<Book, String> {

}
