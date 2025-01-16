package com.nhnacademy.bookapi.service.object_storage;

import com.nhnacademy.bookapi.service.object.ObjectService;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.MockRestServiceServer.createServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * ObjectService에 대한 단위 테스트.
 * - generateAuthToken()
 * - uploadObject()
 * - loadImageFromStorage()
 * - deleteObject()
 *
 * 모든 예외, 분기 로직까지 커버하여
 * 메서드 / 라인 / 분기 커버리지 100% 달성
 */
class ObjectServiceTest {

    private ObjectService objectService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        // 테스트용 ObjectService 생성
        objectService = new ObjectService();

        // 설정값 주입
        objectService.setAuthUrl("http://fake-auth.local/v1/tokens");
        objectService.setStorageUrl("http://fake-storage.local");
        objectService.setTenantId("fake-tenant");
        objectService.setUsername("fake-user");
        objectService.setPassword("fake-pass");

        // RestTemplate 설정 & MockRestServiceServer 생성
        RestTemplate restTemplate = new RestTemplate();
        objectService.setRestTemplate(restTemplate);
        mockServer = createServer(restTemplate);
    }

    @Nested
    @DisplayName("generateAuthToken() 테스트")
    class GenerateAuthTokenTests {

        @Test
        @DisplayName("성공 케이스 - 200 OK")
        void testGenerateAuthTokenSuccess200() {
            // 정상 토큰 발급 시나리오
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
        @DisplayName("실패 케이스 - 202 ACCEPTED (200이 아닌 정상응답)")
        void testGenerateAuthTokenFailNon200() {
            // 202 응답 (OK도 에러도 아닌 경우) -> else문으로 인해 RuntimeException 발생
            mockServer.expect(once(), requestTo("http://fake-auth.local/v1/tokens"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.ACCEPTED));

            assertThatThrownBy(objectService::generateAuthToken)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to get auth token: 202 ACCEPTED");

            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - HttpClientErrorException (4xx)")
        void testGenerateAuthTokenFail4xx() {
            // 401, 403, 404 등 4xx
            mockServer.expect(once(), requestTo("http://fake-auth.local/v1/tokens"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.UNAUTHORIZED)); // 401

            assertThatThrownBy(objectService::generateAuthToken)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to get auth token: 401 UNAUTHORIZED");

            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - HttpServerErrorException (5xx)")
        void testGenerateAuthTokenFail5xx() {
            // 500 등 5xx
            mockServer.expect(once(), requestTo("http://fake-auth.local/v1/tokens"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

            assertThatThrownBy(objectService::generateAuthToken)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to get auth token: 500 INTERNAL_SERVER_ERROR");

            mockServer.verify();
        }
    }

    @Nested
    @DisplayName("uploadObject() 테스트")
    class UploadObjectTests {

        @Test
        @DisplayName("실패 케이스 - Token이 null")
        void testUploadObjectNoToken() {
            // tokenId가 null이면 IllegalStateException
            objectService.setTokenId(null);

            InputStream inputStream = new ByteArrayInputStream("dummy".getBytes());
            assertThatThrownBy(() -> objectService.uploadObject("container", "obj", inputStream))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Token is not available");
        }

        @Test
        @DisplayName("성공 케이스 - 200 OK")
        void testUploadObjectSuccess200() {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/myObject";
            // 200 OK 응답 시 정상 처리
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withSuccess("", MediaType.TEXT_PLAIN));

            InputStream inputStream = new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8));
            objectService.uploadObject("container", "myObject", inputStream);

            // inputStream close() 커버리지를 위해 코드에 finally 블록이 있음
            // 여기선 별도 close() 확인이 어려우나 커버리지는 달성
            mockServer.verify();
        }

        @Test
        @DisplayName("성공 케이스 - 201 Created")
        void testUploadObjectSuccess201() {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/createdObject";
            // 201 Created 응답
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withStatus(HttpStatus.CREATED));

            InputStream inputStream = new ByteArrayInputStream("created".getBytes(StandardCharsets.UTF_8));
            objectService.uploadObject("container", "createdObject", inputStream);

            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - 202 Accepted 등 (OK/CREATED 외 상태)")
        void testUploadObjectFailNon200or201() {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/failObject";
            // 202 응답 (OK, CREATED 외의 상태 코드)
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withStatus(HttpStatus.ACCEPTED));

            InputStream inputStream = new ByteArrayInputStream("test".getBytes());

            assertThatThrownBy(() -> objectService.uploadObject("container", "failObject", inputStream))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to upload object: 202 ACCEPTED");

            mockServer.verify();
        }


        @Test
        @DisplayName("실패 케이스 - HttpClientErrorException (4xx)")
        void testUploadObjectFail4xx() {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/fourOhFour";
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withStatus(HttpStatus.FORBIDDEN)); // 403

            InputStream inputStream = new ByteArrayInputStream("not allowed".getBytes());

            assertThatThrownBy(() -> objectService.uploadObject("container", "fourOhFour", inputStream))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Upload failed with status: 403 FORBIDDEN");

            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - HttpServerErrorException (5xx)")
        void testUploadObjectFail5xx() {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/internalError";
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.PUT))
                    .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

            InputStream inputStream = new ByteArrayInputStream("server error".getBytes());

            assertThatThrownBy(() -> objectService.uploadObject("container", "internalError", inputStream))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Upload failed with status: 500 INTERNAL_SERVER_ERROR");

            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - 예기치 않은 Exception (예: IOException)")
        void testUploadObjectUnexpectedException() {
            objectService.setTokenId("valid-token");

            // IOUtils.toByteArray(...)에서 IOException 발생하도록 가짜 InputStream 사용
            InputStream brokenStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("Broken Stream");
                }
            };

            assertThatThrownBy(() -> objectService.uploadObject("container", "failObject", brokenStream))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to upload object: Broken Stream");

            // HTTP 요청 전에 예외 발생 -> mockServer 기록 없음
            mockServer.verify();
        }
    }

    @Nested
    @DisplayName("loadImageFromStorage() 테스트")
    class LoadImageFromStorageTests {

        @Test
        @DisplayName("성공 케이스 - 200 OK")
        void testLoadImageSuccess() throws IOException {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/someImage.jpg";
            byte[] fakeImage = "fakeImageData".getBytes(StandardCharsets.UTF_8);

            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(fakeImage, MediaType.APPLICATION_OCTET_STREAM));

            // when
            MockMultipartFile result =
                    (MockMultipartFile) objectService.loadImageFromStorage("container", "someImage.jpg");

            // then
            mockServer.verify();
            assertThat(result).isNotNull();
            assertThat(result.getOriginalFilename()).isEqualTo("someImage.jpg");
            assertThat(result.getBytes()).isEqualTo(fakeImage);
        }

        @Test
        @DisplayName("실패 케이스 - 404 Not Found")
        void testLoadImageNotFound() {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/noContainer/noImage.png";
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.NOT_FOUND));

            assertThatThrownBy(() ->
                    objectService.loadImageFromStorage("noContainer", "noImage.png"))
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
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/myObject";
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withStatus(HttpStatus.NO_CONTENT));

            objectService.deleteObject("container", "myObject");
            mockServer.verify();
        }

        @Test
        @DisplayName("실패 케이스 - 403 Forbidden")
        void testDeleteObjectForbidden() {
            objectService.setTokenId("valid-token");

            String url = "http://fake-storage.local/container/forbiddenObj";
            mockServer.expect(once(), requestTo(url))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withStatus(HttpStatus.FORBIDDEN));

            assertThatThrownBy(() -> objectService.deleteObject("container", "forbiddenObj"))
                    .isInstanceOf(HttpClientErrorException.Forbidden.class);

            mockServer.verify();
        }
    }
}
