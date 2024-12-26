package com.nhnacademy.bookapi.service.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.BookCreatorMapRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.BookPopularityRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTypeRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookApiSaveServiceTest {

    @InjectMocks
    private BookApiSaveService bookApiSaveService;

    @Mock
    private  BookApiService bookApiService;

    @Mock
    private  BookRepository bookRepository;
    @Mock
    private  BookCreatorRepository bookCreatorRepository;
    @Mock
    private BookPopularityRepository bookPopularRepository;
    @Mock
    private  BookImageRepository bookImageRepository;
    @Mock
    private  ImageRepository imageRepository;
    @Mock
    private  BookTypeRepository bookTypeRepository;
    @Mock
    private  PublisherRepository publisherRepository;
    @Mock
    private  CategoryRepository categoryRepository;
    @Mock
    private  BookCategoryRepository bookCategoryRepository;
    @Mock
    private  BookCreatorMapRepository bookCreatorMapRepository;

    String jsonString;

    JsonNode mockBookList;
    JsonNode mockBookDetail;


    @BeforeEach
    void setup() throws JsonProcessingException {

        jsonString = "{\n"
            + "    \"isbn13\": \"1234567890123\",\n"
            + "    \"publisher\": \"Test Publisher\",\n"
            + "    \"cover\": \"http://example.com/image.jpg\",\n"
            + "    \"title\": \"Test Book\",\n"
            + "    \"description\": \"This is a test book\",\n"
            + "    \"priceStandard\": 10000,\n"
            + "    \"priceSales\": 9000,\n"
            + "    \"author\": \"Author1(지은이)\",\n"
            + "    \"categoryName\": \"Fiction>Adventure\",\n"
            + "    \"bestRank\": 1\n"
            + "}";
        mockBookList = new ObjectMapper().readTree(jsonString);

        mockBookDetail = new ObjectMapper().readTree("{ \"subInfo\": { \"itemPage\": 350 } }");

    }

//    @Test
//    void testSaveBook() throws Exception {
//
//        Mockito.when(bookApiService.getBookList(Mockito.anyString())).thenReturn(mockBookList);
//        Mockito.when(bookApiService.getBook(Mockito.anyString())).thenReturn(mockBookDetail);
//
//        bookApiSaveService.saveBook("Bestseller");
//
//        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
//
//
//        Mockito.verify(bookRepository, Mockito.times(10)).save(Mockito.any(Book.class));
//
//        List<Book> savedBooks = bookCaptor.getAllValues();
//
//        for (Book savedBook : savedBooks) {
//            assertEquals(350, savedBook.getPage()); // Book의 page 값이 350인지 확인
//        }
//    }






}