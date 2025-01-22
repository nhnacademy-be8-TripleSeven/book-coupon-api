package com.nhnacademy.bookapi.service.object;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Component
public class NaverObjectStorageService {

    @Value("${naver.storage.accessKey}")
    private String accessKey;
    @Value("${naver.storage.secretKey}")
    private String secretKey;
    @Value("${naver.storage.endpoint}")
    private String endpoint;
    @Value("${naver.storage.containerName}")
    private String containerName;

    public S3Client createS3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .region(Region.of("us-east-1"))
                .build();
    }

    public String uploadFile(String objectKey, MultipartFile file) {
        S3Client s3 = createS3Client();

        try (InputStream inputStream = file.getInputStream()) {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(containerName)
                            .key(objectKey)
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }

        return endpoint + "/" + containerName + "/" + objectKey;
    }

    public void deleteFile(String objectKey) {
        S3Client s3 = createS3Client();

        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(containerName)
                .key(objectKey)
                .build());
    }
}
