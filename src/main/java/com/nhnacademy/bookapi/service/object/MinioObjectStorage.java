package com.nhnacademy.bookapi.service.object;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class MinioObjectStorage {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public String uploadImage(String fileName, MultipartFile multipartFile) {
        try {
            // 버킷 없으면 생성
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            // 업로드
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build()
            );

            String presignedObjectUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .method(Method.GET)
                            .build());

            return convertToGatewayUrl(presignedObjectUrl);

        } catch (Exception e) {
            throw new RuntimeException("MinIO 파일 업로드 실패: " + e.getMessage(), e);
        }
    }

    private String convertToGatewayUrl(String presignedObjectUrl) {
        try {
            URI uri = new URI(presignedObjectUrl);
            String path = uri.getPath(); // /nhn24-bucket/9788936434120_cover.jpg
            String query = uri.getQuery(); // X-Amz-Algorithm=...

            // Gateway URL로 변환
            String gatewayUrl = "https://nhn24.shop/storage" + path;
            if (query != null) {
                gatewayUrl += "?" + query;
            }

            return gatewayUrl;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL format", e);
        }
    }
}
