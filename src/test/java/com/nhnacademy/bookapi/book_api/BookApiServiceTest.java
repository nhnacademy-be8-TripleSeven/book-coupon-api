package com.nhnacademy.bookapi.book_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookApiServiceTest {

    @InjectMocks
    private BookApiService bookApiService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${aladin.api.key}")
    private String apiKey = "testApiKey";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookApiService = new BookApiService(objectMapper);
    }



    @Test
    void testGetBook_Failure() throws Exception {
        // Given
        String isbn = "9781234567890";
        String url = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=" + apiKey +
            "&itemIdType=ISBN13&ItemId=" + isbn + "&output=JS&Version=20131101&OptResult=Toc";

        when(restTemplate.getForObject(url, String.class)).thenThrow(new RuntimeException("API call failed"));

        // When
        Exception exception = assertThrows(Exception.class, () -> bookApiService.getBook(isbn));

        // Then
        assertEquals("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.path(String)\" because \"rootNode\" is null", exception.getMessage());

    }
}
