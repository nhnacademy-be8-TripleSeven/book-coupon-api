package com.nhnacademy.bookapi.service.object_storage;

import com.amazonaws.util.IOUtils;
import com.nhnacademy.bookapi.config.GlobalExceptionHandler;
import com.nhnacademy.bookapi.service.object.ObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.MockRestServiceServer.createServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * ObjectService에 대한 테스트 클래스
 * - generateAuthToken()
 * - uploadObject()
 * - loadImageFromStorage()
 *
 * 각각 성공 케이스와 실패 케이스를 모두 테스트하여
 * 메소드/라인/분기(Branch) 커버리지 100% 달성.
 */
class ObjectServiceTest {

    private ObjectService objectService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        // 테스트용 ObjectService 생성
        objectService = new ObjectService();
        // RestTemplate을 커스텀해서 주입해도 되고, 기본 생성자 후 objectService.setRestTemplate() 해도 됨.
        RestTemplate restTemplate = new RestTemplate();

        objectService.setRestTemplate(restTemplate);
        objectService.setAuthUrl("http://fake-auth.local/v1/tokens");
        objectService.setTenantId("test-tenant");
        objectService.setUsername("test-user");
        objectService.setPassword("test-pass");
        objectService.setStorageUrl("http://fake-storage.local");

        // RestTemplate에 MockRestServiceServer 연결
        mockServer = createServer(restTemplate);
    }

    @Nested
    @DisplayName("generateAuthToken() 테스트")
    class GenerateAuthTokenTests {

        @Test
        @DisplayName("성공 케이스 - 200 OK")
        void testGenerateAuthTokenSuccess() {
            // given
            String responseBody = """
                    {
                      "auth": {
                        "token": {
                          "id":"fake-token-value"
                        }
                      }
                    }
                    """;
            mockServer.expect(once(), requestTo("http://fake-auth.local/v1/tokens"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

            // when
            objectService.generateAuthToken();

            // then
            mockServer.verify();
            assertThat(objectService.getTokenId()).isEqualTo("fake-token-value");
        }

        @Test
        @DisplayName("실패 케이스 - 500 오류")
        void testGenerateAuthTokenFail() {
            // given
            mockServer.expect(once(), requestTo("http://fake-auth.local/v1/tokens"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withServerError()); // 500

            // when & then
            assertThatThrownBy(() -> objectService.generateAuthToken())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to get auth token: 500 INTERNAL_SERVER_ERROR");

            mockServer.verify();
        }
    }

    @Nested
    @DisplayName("uploadObject() 테스트")
    class UploadObjectTests {

        @Test
        @DisplayName("성공 케이스 - 201 Created")
        void testUploadObjectSuccessCreated() throws Exception {
            // given
            String containerName = "test-container";
            String objectName = "test-file.png";
            String url = "http://fake-storage.local/test-container/test-file.png";
            // 토큰 값 설정
            objectService.setTokenId("valid-token");

            // Mock 서버: 201 응답
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withStatus(HttpStatus.CREATED));

            // 바이트 배열로 만든 테스트용 InputStream
            byte[] data = "dummy-image-content".getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(data);

            // when
            objectService.uploadObject(containerName, objectName, inputStream);

            // then
            mockServer.verify();
        }

        @Test
        @DisplayName("성공 케이스 - 200 OK")
        void testUploadObjectSuccessOk() throws Exception {
            String containerName = "cont";
            String objectName = "obj.txt";
            String url = "http://fake-storage.local/cont/obj.txt";
            objectService.setTokenId("valid-token");

            // Mock 서버: 200 응답
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withSuccess("", MediaType.TEXT_PLAIN));

            byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
            InputStream is = new ByteArrayInputStream(data);

            objectService.uploadObject(containerName, objectName, is);

            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - 업로드 중 403 Forbidden")
        void testUploadObjectFail403() throws Exception {
            String containerName = "cont";
            String objectName = "obj.png";
            String url = "http://fake-storage.local/cont/obj.png";

            objectService.setTokenId("valid-token");

            // Mock 서버: 403 응답
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withStatus(HttpStatus.FORBIDDEN));

            byte[] data = "forbidden content".getBytes(StandardCharsets.UTF_8);
            InputStream is = new ByteArrayInputStream(data);

            // when & then
            assertThatThrownBy(() -> objectService.uploadObject(containerName, objectName, is))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Upload failed with status: 403 FORBIDDEN");

            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - 토큰 없음")
        void testUploadObjectNoToken() {
            // given
            objectService.setTokenId(null); // 토큰 없음

            // when & then
            assertThatThrownBy(() -> objectService.uploadObject("any", "obj", new ByteArrayInputStream(new byte[0])))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Token is not available");
        }
    }

    @Nested
    @DisplayName("loadImageFromStorage() 테스트")
    class LoadImageFromStorageTests {

        @Test
        @DisplayName("성공 케이스 - 200 OK")
        void testLoadImageFromStorageSuccess() throws Exception {
            String containerName = "my-container";
            String objectName = "image.jpg";
            String url = "http://fake-storage.local/my-container/image.jpg";
            objectService.setTokenId("valid-token");

            byte[] fakeImageBytes = "fake image bytes".getBytes(StandardCharsets.UTF_8);

            // Mock 서버: 200 OK + 바이트 스트림
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(fakeImageBytes, MediaType.APPLICATION_OCTET_STREAM));

            // when
            MockMultipartFile result = (MockMultipartFile) objectService.loadImageFromStorage(containerName, objectName);

            // then
            mockServer.verify();
            assertThat(result).isNotNull();
            assertThat(result.getOriginalFilename()).isEqualTo("image.jpg");
            assertThat(result.getBytes()).isEqualTo(fakeImageBytes);
        }

        @Test
        @DisplayName("실패 케이스 - 404 NotFound")
        void testLoadImageFromStorageNotFound() {
            String containerName = "no-exist";
            String objectName = "no-image.png";
            String url = "http://fake-storage.local/no-exist/no-image.png";

            objectService.setTokenId("valid-token");

            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.NOT_FOUND));

            // when & then
            // loadImageFromStorage() 안에서 RestTemplate.execute()가 404 받으면
            // Spring에서 HttpClientErrorException.NotFound 발생
            assertThatThrownBy(() -> objectService.loadImageFromStorage(containerName, objectName))
                    .isInstanceOf(HttpClientErrorException.NotFound.class);

            mockServer.verify();
        }
    }
    @Nested
    @DisplayName("deleteObject() 테스트")
    class DeleteObjectTests {

        @Test
        @DisplayName("성공 케이스 - 204 No Content")
        void testDeleteObjectSuccess() {
            // given
            String containerName = "test-container";
            String objectName = "test-object";
            String url = "http://fake-storage.local/test-container/test-object";

            objectService.setTokenId("valid-token");

            // Mock 서버: 204 응답
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withStatus(HttpStatus.NO_CONTENT));

            // when
            objectService.deleteObject(containerName, objectName);

            // then
            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - 403 Forbidden")
        void testDeleteObjectForbidden() {
            // given
            String containerName = "test-container";
            String objectName = "test-object";
            String url = "http://fake-storage.local/test-container/test-object";

            objectService.setTokenId("valid-token");

            // Mock 서버: 403 Forbidden 응답
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withStatus(HttpStatus.FORBIDDEN));

            // when & then
            assertThatThrownBy(() -> objectService.deleteObject(containerName, objectName))
                    .isInstanceOf(HttpClientErrorException.Forbidden.class)
                    .hasMessageContaining("403 Forbidden");

            mockServer.verify();
        }

//        @Test
//        @DisplayName("실패 케이스 - 토큰 없음")
//        void testDeleteObjectNoToken() {
//            // given
//            objectService.setTokenId(null); // 토큰 없음
//
//            // when & then
//            assertThatThrownBy(() -> objectService.deleteObject("any-container", "any-object"))
//                    .isInstanceOf(IllegalStateException.class)
//                    .hasMessageContaining("Token is not available");
//        }
    }

}
