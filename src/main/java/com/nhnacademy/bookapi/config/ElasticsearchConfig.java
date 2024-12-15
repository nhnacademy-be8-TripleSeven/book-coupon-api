package com.nhnacademy.bookapi.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
    basePackages = "com.nhnacademy.bookapi.elasticsearch.repository"
)
public class ElasticsearchConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder(new HttpHost("115.94.72.19", 9200)).build();
    }

    @Bean
    public RestClientTransport restClientTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClientTransport restClientTransport) {
        return new ElasticsearchClient(restClientTransport);
    }
}
