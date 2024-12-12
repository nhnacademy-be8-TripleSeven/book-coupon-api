package com.nhnacademy.bookapi.service.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class BookApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BookApiService bookApiService;

    @Value("${aladin.api.key}")
    private String apiKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBookList() throws Exception {
        // Given
        String bookType = "Bestseller";
        String mockUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=" + apiKey + "&QueryType=" + bookType + "&MaxResults=50&start=1&SearchTarget=Book&output=js&Version=20131101";
        String mockResponse = "{\"item\": [{\"title\": \"Test Book\"}]}";

        when(restTemplate.getForObject(mockUrl, String.class)).thenReturn(mockResponse);
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(mockResponse)).thenReturn(mockJsonNode);
        when(mockJsonNode.path("item")).thenReturn(mockJsonNode);


    }

    @Test
    void testGetBook() throws Exception {
        // Given
        String isbn = "1234567890123";
        String mockUrl = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=" + apiKey + "&itemIdType=ISBN13&ItemId=" + isbn + "&output=js&Version=20131101&OptResult=ebookList,usedList,reviewList";
        String mockResponse = "{\"item\": [{\"title\": \"Detailed Test Book\"}]}";

        when(restTemplate.getForObject(mockUrl, String.class)).thenReturn(mockResponse);
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(mockResponse)).thenReturn(mockJsonNode);
        when(mockJsonNode.path("item")).thenReturn(mockJsonNode);

    }
}
