package com.nhnacademy.bookapi.book_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.dto.book.BookApiDTO;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.mapper.RoleMapper;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.image.ImageService;
import com.nhnacademy.bookapi.service.object.ObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
class BookApiSaveServiceTest {

    @Spy
    @InjectMocks
    private BookApiSaveService bookApiSaveService;

    @Mock
    private BookApiService bookApiService;

    @Mock
    private BookIndexRepository bookIndexRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCreatorRepository bookCreatorRepository;

    @Mock
    private BookPopularityRepository bookPopularRepository;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private BookTypeRepository bookTypeRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private BookCreatorMapRepository bookCreatorMapRepository;

    @Mock
    private BookCoverImageRepository bookCoverImageRepository;

    @Mock
    private ObjectService objectService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAladinBookByIsbn_BookExists() throws Exception {
        // Given
        String isbn = "1234567890123";

        // Mocking bookApiService.getBook(isbn) to return a JsonNode with matching isbn13
        String jsonResponse = "{"
            + "\"isbn13\": \"" + isbn + "\","
            + "\"title\": \"Test Book\","
            + "\"pubDate\": \"2023-10-01\","
            + "\"description\": \"A test book description.\","
            + "\"priceStandard\": 20000,"
            + "\"priceSales\": 15000,"
            + "\"cover\": \"http://example.com/cover.jpg\","
            + "\"publisher\": \"Test Publisher\","
            + "\"bestRank\": 1,"
            + "\"author\": \"Author Name\","
            + "\"categoryName\": \"Fiction > Adventure\""
            + "}";

        JsonNode bookNode = objectMapper.readTree("[" + jsonResponse + "]");

        when(bookApiService.getBook(isbn)).thenReturn(bookNode);
        when(bookRepository.existsByIsbn13(isbn)).thenReturn(false);

        // When
        BookApiDTO result = bookApiSaveService.getAladinBookByIsbn(isbn);

        // Then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(isbn, result.getIsbn());
        assertEquals(LocalDate.parse("2023-10-01"), result.getPublishedDate());
        assertEquals("A test book description.", result.getDescription());
        assertEquals(20000, result.getRegularPrice());
        assertEquals(15000, result.getSalePrice());
        assertEquals(1, result.getCoverImage().size());
        assertEquals("http://example.com/cover.jpg", result.getCoverImage().get(0));
        assertEquals(1000, result.getStock());
        assertEquals(0, result.getPage());
        assertEquals("Test Publisher", result.getPublisherName());

        // Verify that repository methods were called
        verify(bookRepository, times(1)).existsByIsbn13(isbn);
        verify(bookApiService, times(1)).getBook(isbn);
    }

}
