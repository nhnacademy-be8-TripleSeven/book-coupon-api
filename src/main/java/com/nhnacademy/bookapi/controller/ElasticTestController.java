package com.nhnacademy.bookapi.controller;

import java.util.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ElasticTestController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String elasticsearchUrl = "http://115.94.72.197:9200";

    @GetMapping("/test-connection")
    public String testConnection() {
        // 인증 정보를 Base64로 인코딩
        String auth = "elastic:nhnacademy123!";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        // Authorization 헤더에 인증 정보 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedAuth);

        // 요청 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Elasticsearch로 요청 전송
        ResponseEntity<String> response = restTemplate.exchange(elasticsearchUrl, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
