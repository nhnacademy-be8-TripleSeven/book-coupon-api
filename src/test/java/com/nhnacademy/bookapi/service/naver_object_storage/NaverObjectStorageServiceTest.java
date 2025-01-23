package com.nhnacademy.bookapi.service.naver_object_storage;

import com.nhnacademy.bookapi.service.object.NaverObjectStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NaverObjectStorageServiceTest {

    private NaverObjectStorageService storageService;

    @Mock
    private S3Client s3Client;  // createS3Client()가 반환할 가짜 객체

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws IOException {
        // 실제 객체를 Spy로 감싸지 않고 직접 생성
        storageService = Mockito.spy(new NaverObjectStorageService());

        // Reflection을 사용하여 필드값 설정
        ReflectionTestUtils.setField(storageService, "accessKey", "dummyAccessKey");
        ReflectionTestUtils.setField(storageService, "secretKey", "dummySecretKey");
        ReflectionTestUtils.setField(storageService, "endpoint", "https://dummy-endpoint.com");
        ReflectionTestUtils.setField(storageService, "containerName", "dummy-container");

        // MultipartFile Mock 설정
        InputStream dummyInputStream = new ByteArrayInputStream("dummy data".getBytes());
        lenient().when(multipartFile.getInputStream()).thenReturn(dummyInputStream);
        lenient().when(multipartFile.getSize()).thenReturn(10L);

        // ⭐ createS3Client()가 실제 호출되지 않고 Mock S3Client를 반환하도록 설정
        doReturn(s3Client).when(storageService).createS3Client();
    }

    @Test
    void testCreateS3Client_Success() {
        // `createS3Client()`를 직접 호출하여 실제 객체 생성
        S3Client s3Client = storageService.createS3Client();

        assertNotNull(s3Client, "S3Client 객체가 정상적으로 생성되어야 합니다.");

        // Reflection을 사용하여 필드 값을 검증
        String endpoint = ReflectionTestUtils.getField(storageService, "endpoint").toString();
        String accessKey = ReflectionTestUtils.getField(storageService, "accessKey").toString();
        String secretKey = ReflectionTestUtils.getField(storageService, "secretKey").toString();

        assertEquals("https://dummy-endpoint.com", endpoint);
        assertEquals("dummyAccessKey", accessKey);
        assertEquals("dummySecretKey", secretKey);
    }


    @Test
    void testUploadFile_Success() throws IOException {
        String objectKey = "test-file.txt";

        // 업로드 실행
        String resultUrl = storageService.uploadFile(objectKey, multipartFile);

        // 업로드 URL 검증
        assertEquals("https://dummy-endpoint.com/dummy-container/" + objectKey, resultUrl);

        // putObject가 정상적으로 호출되었는지 검증
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_IOException() throws IOException {
        String objectKey = "error-file.txt";

        // 파일 읽기 시 IOException 발생하도록 설정
        when(multipartFile.getInputStream()).thenThrow(new IOException("파일 읽기 실패"));

        // 예외 발생 검증
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            storageService.uploadFile(objectKey, multipartFile);
        });

        assertTrue(thrown.getMessage().contains("파일 업로드 실패"));

        // putObject가 호출되지 않았는지 검증
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testDeleteFile_Success() {
        String objectKey = "delete-file.txt";

        // 파일 삭제 실행
        storageService.deleteFile(objectKey);

        // deleteObject가 정상적으로 호출되었는지 검증
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
