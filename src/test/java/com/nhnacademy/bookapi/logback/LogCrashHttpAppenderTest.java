package com.nhnacademy.bookapi.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LogCrashHttpAppenderTest {

    @InjectMocks
    private LogCrashHttpAppender appender;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ILoggingEvent loggingEvent;

    @Mock
    private Context context;

    @Mock
    private StatusManager statusManager;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> httpEntityCaptor;

    private static final String TEST_URL = "http://test-url.com/log";
    private static final String TEST_APP_KEY = "testAppKey";
    private static final String TEST_LOG_SOURCE = "testSource";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appender.setUrl(TEST_URL);
        appender.setAppKey(TEST_APP_KEY);
        appender.setLogSource(TEST_LOG_SOURCE);
        appender.setContext(context);
        when(context.getStatusManager()).thenReturn(statusManager);
    }

    @Test
    void testAppend_Success() {
        // Given
        ReflectionTestUtils.setField(appender, "restTemplate", restTemplate); // Reflection 사용
        when(loggingEvent.getFormattedMessage()).thenReturn("Test log message");
        when(restTemplate.postForEntity(eq(TEST_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("Success"));

        // When
        appender.append(loggingEvent);

        // Then
        verify(restTemplate, times(1)).postForEntity(eq(TEST_URL), httpEntityCaptor.capture(), eq(String.class));

        HttpEntity<String> capturedEntity = httpEntityCaptor.getValue();
        assertNotNull(capturedEntity.getBody());
        assertTrue(capturedEntity.getBody().contains("Test log message"));
        assertTrue(capturedEntity.getBody().contains(TEST_APP_KEY));
        assertTrue(capturedEntity.getBody().contains(TEST_LOG_SOURCE));
    }



    @Test
    void testAppend_Failure() {
        // Given
        when(loggingEvent.getFormattedMessage()).thenReturn("Test log message");
        doThrow(new RuntimeException("Test exception"))
                .when(restTemplate).postForEntity(eq(TEST_URL), any(HttpEntity.class), eq(String.class));

        // When
        appender.append(loggingEvent);

        // Then
        verify(statusManager, times(1)).add(any(Status.class));
    }
}
