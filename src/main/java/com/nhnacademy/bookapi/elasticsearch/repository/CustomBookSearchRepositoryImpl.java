package com.nhnacademy.bookapi.elasticsearch.repository;


import lombok.RequiredArgsConstructor;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomBookSearchRepositoryImpl implements CustomBookSearchRepository {

    private ElasticsearchTemplate elasticsearchTemplate;


}
