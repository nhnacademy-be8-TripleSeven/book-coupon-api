package com.nhnacademy.bookapi.service.object;


import com.amazonaws.util.IOUtils;
import java.net.HttpURLConnection;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import org.springframework.web.multipart.MultipartFile;

@Data
@Component
public class ObjectService {

    private String tokenId;
    @Value("${object.storage.url}")
    private String storageUrl;
    @Value("${object.auth.url}")
    private String authUrl;
    @Value("${object.tenantId}")
    private String tenantId;
    @Value("${object.username}")
    private String username;
    @Value("${object.password}")
    private String password;
    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        generateAuthToken();
    }

    private String getUrl(@NonNull String containerName, @NonNull String objectName) {
        return this.getStorageUrl() + "/" + containerName + "/" + objectName;
    }

    //토큰 발급
    public void generateAuthToken() {

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 구성
        String payload = String.format(
                "{\"auth\": {\"tenantId\": \"%s\", \"passwordCredentials\": {\"username\": \"%s\", \"password\": \"%s\"}}}",
                this.tenantId, this.username, this.password
        );

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

//        // API 요청 및 응답 처리
//        ResponseEntity<String> response = restTemplate.exchange(
//                this.authUrl, HttpMethod.POST, requestEntity, String.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            String responseBody = response.getBody();
//
//            // 응답에서 토큰 추출 - 토큰 저장
//            this.tokenId = responseBody.split("\"id\":\"")[1].split("\"")[0];
//            System.out.println("New token issued: " + this.tokenId);
//        } else {
//            throw new RuntimeException("Failed to get auth token: " + response.getStatusCode());
//        }
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    this.authUrl, HttpMethod.POST, requestEntity, String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                this.tokenId = responseBody.split("\"id\":\"")[1].split("\"")[0];
            } else {
                throw new RuntimeException("Failed to get auth token: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new RuntimeException("Failed to get auth token: " + ex.getStatusCode(), ex);
        }
    }
    // cloud object storage에 올라가 있는 이미지를 개인 컴퓨터에 다운로드하는 메소드
//    public File downloadObject(String containerName, String objectName, String downloadPath) {
//        String url = this.getUrl(containerName, objectName);
//
//        RequestCallback callback = (request) -> {
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("X-Auth-Token", this.tokenId);
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
//
//        };
//        ResponseExtractor<File> extractor = (clientHttpResponse) -> {
//            File ret = new File(downloadPath + "/" + objectName);
//            StreamUtils.copy(clientHttpResponse.getBody(), Files.newOutputStream(ret.toPath()));
//            return ret;
//        };
//        return this.restTemplate.execute(url, HttpMethod.GET, callback, extractor);
//    }
    //nhn cloud object storage에 업로드
    public void uploadObject(String containerName, String objectName, final InputStream inputStream) {
        if (this.tokenId == null) {
            throw new IllegalStateException("Token is not available. Please issue a token first.");
        }

        String url = this.getUrl(containerName, objectName); // 업로드 대상 URL 생성

        try {
            // InputStream을 ByteArray로 변환
            byte[] byteArray = IOUtils.toByteArray(inputStream);
            ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray) {
                @Override
                public String getFilename() {
                    return objectName; // 파일 이름 설정
                }
            };

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Auth-Token", this.tokenId);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 요청 본문 설정
            HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(byteArrayResource, headers);

            // RestTemplate을 사용한 PUT 요청 실행
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.PUT, requestEntity, String.class
            );

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Upload success: " + url);
            } else {
                throw new RuntimeException("Upload failed with status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new RuntimeException("Upload failed with status: " + ex.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during upload", e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public MultipartFile loadImageFromStorage(String containerName, String objectName) {
        String url = this.getUrl(containerName, objectName);

        RequestCallback callback = (request) -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Auth-Token", this.tokenId);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        };
        ResponseExtractor<MultipartFile> extractor = (clientHttpResponse) -> {
            byte[] bytes = StreamUtils.copyToByteArray(clientHttpResponse.getBody());
            return new MockMultipartFile(
                "file",                     // 필드 이름
                objectName,                 // 파일 이름
                "application/octet-stream", // MIME 타입 (필요에 따라 변경 가능)
                new ByteArrayInputStream(bytes) // 파일 데이터
            );
        };

        return this.restTemplate.execute(url, HttpMethod.GET, callback, extractor);
    }

    public void deleteObject(String containerName, String objectName) {

        String url = this.getUrl(containerName, objectName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", this.tokenId);
        HttpEntity<String> requestHttpEntity = new HttpEntity<>(null, headers);

        this.restTemplate.exchange(url, HttpMethod.DELETE, requestHttpEntity, String.class);
    }
}

