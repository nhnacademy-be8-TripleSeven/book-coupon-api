package com.nhnacademy.bookapi.config;

import com.nhnacademy.bookapi.skm.KeyProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.util.List;
import java.util.Objects;
import javax.net.ssl.SSLContext;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;


import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


// 생략된 import는 유지
@Configuration
@EnableConfigurationProperties(KeyProperties.class)
@RequiredArgsConstructor
public class KeyConfig {

    private final KeyProperties keyProperties;

    public String keyStore(String keyId) {
        try {
            // HttpClient 4.x 객체 생성
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // 올바른 RequestFactory 설정
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            RestTemplate restTemplate = new RestTemplate(requestFactory);

            URI uri = UriComponentsBuilder
                .fromUriString(keyProperties.getUrl())
                .path(keyProperties.getPath())
                .encode()
                .build()
                .expand(keyProperties.getAppKey(), keyId)
                .toUri();

            return Objects.requireNonNull(
                    restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), KeyResponseDto.class)
                        .getBody())
                .getBody()
                .getSecret();
        } catch (Exception e) {
            throw new RuntimeException("Error in keyStore: " + e.getMessage(), e);
        }
    }
}
