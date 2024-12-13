package com.nhnacademy.bookapi.logback;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class LogCrashHttpAppender extends AppenderBase<ILoggingEvent> {

    private static final RestTemplate restTemplate = new RestTemplate();
    private String appKey;
    private String url;
    private String logSource = "http";

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLogSource(String logSource) {
        this.logSource = logSource;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String message = eventObject.getFormattedMessage();
            String jsonPayload = String.format(
                "{" +
                    "\"projectName\": \"%s\"," +
                    "\"projectVersion\": \"1.0.0\"," +
                    "\"logVersion\": \"v2\"," +
                    "\"body\": \"%s\"," +
                    "\"logSource\": \"%s\"," +
                    "\"logType\": \"log\"," +
                    "\"host\": \"localhost\"" +
                    "}", appKey, message, logSource);

            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            addError("Failed to send log to NHN Cloud Log & Crash", e);
        }
    }
}