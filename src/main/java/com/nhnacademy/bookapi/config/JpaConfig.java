package com.nhnacademy.bookapi.config;

import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.nhnacademy.bookapi.repository",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ElasticSearchBookSearchRepository.class)
)
public class JpaConfig {

}
