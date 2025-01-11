package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.BookCreatDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCoverImage;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.BookImage;
import com.nhnacademy.bookapi.entity.BookPopularity;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.entity.Publisher;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.BookCouponRepository;
import com.nhnacademy.bookapi.repository.BookPopularityRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;
import com.nhnacademy.bookapi.repository.ReviewRepository;
import com.nhnacademy.bookapi.repository.WrapperRepository;
import com.nhnacademy.bookapi.service.book_index.BookIndexService;
import com.nhnacademy.bookapi.service.book_type.BookTypeService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;
import com.nhnacademy.bookapi.service.category.CategoryService;
import com.nhnacademy.bookapi.service.image.ImageService;
import com.nhnacademy.bookapi.service.object.ObjectService;
import com.nhnacademy.bookapi.service.review.ReviewService;
import com.nhnacademy.bookapi.service.tag.TagService;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BookMultiTableServiceTest {

    @InjectMocks
    private BookMultiTableService bookMultiTableService;

    @Mock
    private ObjectService objectService;

    @Mock
    private BookService bookService;

    @Mock
    private ImageService imageService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookCreatorService bookCreatorService;

    @Mock
    private TagService tagService;

    @Mock
    private BookIndexService bookIndexService;

    @Mock
    private BookTypeService bookTypeService;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private BookCouponRepository couponRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private WrapperRepository wrapperRepository;

    @Mock
    private BookPopularityRepository popularityRepository;

    @Mock
    private BookPopularityRepository bookPopularityRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private ReviewService reviewService;

    @Mock
    private CategoryRepository categoryRepository;

    // 상수 변수 설정
    private final String storageUrl = "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e";
    private final String containerName = "triple-seven";

    @BeforeEach
    void setUp() {
        // 필요한 경우 공통 설정을 추가할 수 있습니다.
    }

    // 1. getAdminBookById 메서드 테스트
    @Test
    void testGetAdminBookById_Success() {
        // Given
        Long bookId = 1L;
        BookDTO bookDTO = BookDTO.builder()
            .id(bookId)
            .title("Test Title")
            .isbn("1234567890")
            .categories(new ArrayList<>())
            .bookTypes(new ArrayList<>())
            .authors(new ArrayList<>())
            .tags(new ArrayList<>())
            .publishedDate(LocalDate.of(2023, 10, 1))
            .description("A test book.")
            .regularPrice(1000)
            .salePrice(800)
            .index("Index Text")
            .coverImage(new ArrayList<>())
            .detailImage(new ArrayList<>())
            .stock(50)
            .page(300)
            .build();

        when(bookService.getBookById(bookId)).thenReturn(bookDTO);
        when(imageService.getBookCoverImages(bookId)).thenReturn(Collections.emptyList());
        when(imageService.getBookDetailImages(bookId)).thenReturn(Collections.emptyList());
        when(categoryService.updateCategoryList(bookId)).thenReturn(Collections.emptyList());
        when(bookCreatorService.bookCreatorList(bookId)).thenReturn(Collections.emptyList());
        when(tagService.getTagName(bookId)).thenReturn(Collections.emptyList());
        when(bookTypeService.getUpdateBookTypeList(bookId)).thenReturn(Collections.emptyList());
        when(bookIndexService.getBookIndexList(bookId)).thenReturn("Expected Index String");

        // When
        BookDTO result = bookMultiTableService.getAdminBookById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        verify(bookService).getBookById(bookId);
        verify(imageService).getBookCoverImages(bookId);
        verify(imageService).getBookDetailImages(bookId);
        verify(categoryService).updateCategoryList(bookId);
        verify(bookCreatorService).bookCreatorList(bookId);
        verify(tagService).getTagName(bookId);
        verify(bookTypeService).getUpdateBookTypeList(bookId);
        verify(bookIndexService).getBookIndexList(bookId);
    }

    // 2. getAdminBookSearch 메서드 테스트
    @Test
    void testGetAdminBookSearch_Success() {
        // Given
        String keyword = "Java";
        Pageable pageable = PageRequest.of(0, 10);
        BookDTO bookDTO = BookDTO.builder()
            .id(1L)
            .title("Java Programming")
            .isbn("0987654321")
            .categories(new ArrayList<>())
            .bookTypes(new ArrayList<>())
            .authors(new ArrayList<>())
            .tags(new ArrayList<>())
            .publishedDate(LocalDate.of(2023, 10, 1))
            .description("A Java book.")
            .regularPrice(1500)
            .salePrice(1200)
            .index("Java Index")
            .coverImage(new ArrayList<>())
            .detailImage(new ArrayList<>())
            .stock(60)
            .page(350)
            .build();
        Page<BookDTO> bookPage = new PageImpl<>(Arrays.asList(bookDTO));

        when(bookService.getBookList(keyword, pageable)).thenReturn(bookPage);
        when(imageService.getBookCoverImages(1L)).thenReturn(Collections.emptyList());
        when(imageService.getBookDetailImages(1L)).thenReturn(Collections.emptyList());
        when(categoryService.updateCategoryList(1L)).thenReturn(Collections.emptyList());
        when(bookCreatorService.bookCreatorList(1L)).thenReturn(Collections.emptyList());
        when(tagService.getTagName(1L)).thenReturn(Collections.emptyList());
        when(bookTypeService.getUpdateBookTypeList(1L)).thenReturn(Collections.emptyList());
        when(bookIndexService.getBookIndexList(1L)).thenReturn("Expected Index String");

        // When
        Page<BookDTO> result = bookMultiTableService.getAdminBookSearch(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookService).getBookList(keyword, pageable);
        verify(imageService).getBookCoverImages(1L);
        verify(imageService).getBookDetailImages(1L);
        verify(categoryService).updateCategoryList(1L);
        verify(bookCreatorService).bookCreatorList(1L);
        verify(tagService).getTagName(1L);
        verify(bookTypeService).getUpdateBookTypeList(1L);
        verify(bookIndexService).getBookIndexList(1L);
    }


    @Test
    void testCreateBook_AlreadyExists() throws IOException {
        // Given
        BookCreatDTO bookCreatDTO = BookCreatDTO.builder()
            .title("Existing Book")
            .isbn("1234567890")
            .categories(Arrays.asList(new CategoryDTO("Non-Fiction", 1)))
            .bookTypes(Arrays.asList(new BookTypeDTO("PAPERBACK", 2)))
            .authors(Arrays.asList(new BookCreatorDTO("Existing Author", "AUTHOR")))
            .publishedDate(LocalDate.of(2022, 5, 15))
            .description("An existing book.")
            .regularPrice(1500)
            .salePrice(1200)
            .page(400)
            .stock(60)
            .index("Existing Index")
            .coverImages(Collections.emptyList())
            .detailImages(Collections.emptyList())
            .publisherName("Existing Publisher")
            // .id(1L) // 제거
            .build();

        when(bookService.existsBookByIsbn("1234567890")).thenReturn(true);

        // When
        bookMultiTableService.createBook(bookCreatDTO);

        // Then
        verify(bookService).existsBookByIsbn("1234567890");
        verify(bookService, times(0)).createBook(any(Book.class));
        verify(publisherRepository, times(0)).findByName(anyString());
        verify(publisherRepository, times(0)).save(any(Publisher.class));
        verify(bookCreatorService, times(0)).saveBookCreator(any(BookCreator.class), any(BookCreatorMap.class));
        verify(bookCategoryRepository, times(0)).save(any(BookCategory.class));
        verify(bookPopularityRepository, times(0)).save(any(BookPopularity.class));
        verify(imageService, times(0)).bookCoverSave(any(Image.class), any(BookCoverImage.class));
        verify(imageService, times(0)).bookDetailSave(any(Image.class), any(BookImage.class));
    }

    // 4. updateBook 메서드 테스트



    // 6. uploadCoverImageToStorage 메서드 테스트
    @Test
    void testUploadCoverImageToStorage_Success() throws IOException {
        // Given
        MultipartFile multipartFile = mock(MultipartFile.class);
        String objectName = "isbn_cover.jpg";
        String expectedUrl = storageUrl + "/" + containerName + "/" + objectName;
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        // When
        String result = bookMultiTableService.uploadCoverImageToStorage(objectService, multipartFile, objectName);

        // Then
        assertEquals(expectedUrl, result);
        verify(objectService).uploadObject(containerName, objectName, inputStream);
    }

    @Test
    void testUploadCoverImageToStorage_IOException() throws IOException {
        // Given
        MultipartFile multipartFile = mock(MultipartFile.class);
        String objectName = "isbn_cover.jpg";
        when(multipartFile.getInputStream()).thenThrow(new IOException("Failed to read input stream"));

        // When & Then
        assertThrows(IOException.class, () -> {
            bookMultiTableService.uploadCoverImageToStorage(objectService, multipartFile, objectName);
        });

        verify(objectService, times(0)).uploadObject(anyString(), anyString(), any());
    }

    // 7. loadImageTOStorage 메서드 테스트
    @Test
    void testLoadImageTOStorage_Success() {
        // Given
        String objectName = "isbn_cover.jpg";
        MultipartFile expectedFile = mock(MultipartFile.class);
        when(objectService.loadImageFromStorage(containerName, objectName)).thenReturn(expectedFile);

        // When
        MultipartFile result = bookMultiTableService.loadImageTOStorage(objectService, objectName);

        // Then
        assertEquals(expectedFile, result);
        verify(objectService).loadImageFromStorage(containerName, objectName);
    }
}
