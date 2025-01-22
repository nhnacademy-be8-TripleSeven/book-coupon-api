package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.BookCreatDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookOrderDetailResponse;
import com.nhnacademy.bookapi.dto.book.BookOrderRequestDTO;
import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.StockUnavailableException;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.book_index.BookIndexService;
import com.nhnacademy.bookapi.service.book_popularity.BookPopularityService;
import com.nhnacademy.bookapi.service.book_tag.BookTagService;
import com.nhnacademy.bookapi.service.book_type.BookTypeService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;
import com.nhnacademy.bookapi.service.category.CategoryService;
import com.nhnacademy.bookapi.service.image.ImageService;
import com.nhnacademy.bookapi.service.object.NaverObjectStorageService;
import com.nhnacademy.bookapi.service.review.ReviewService;
import com.nhnacademy.bookapi.service.tag.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookMultiTableServiceTest {

    @InjectMocks
    private BookMultiTableService bookMultiTableService;

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
    private BookCouponRepository bookCouponRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;


    @Mock
    private BookTypeRepository bookTypeRepository;

    @Mock
    private BookTagService bookTagService;

    @Mock
    private NaverObjectStorageService naverObjectStorageService;

    @Mock
    private BookPopularityService bookPopularityService;




    @BeforeEach
    void setUp() {

    }

    // 1. getAdminBookById method test
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
        when(categoryService.getCategoryListByBookId(bookId)).thenReturn(Collections.emptyList());
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
        verify(categoryService).getCategoryListByBookId(bookId);
        verify(bookCreatorService).bookCreatorList(bookId);
        verify(tagService).getTagName(bookId);
        verify(bookTypeService).getUpdateBookTypeList(bookId);
        verify(bookIndexService).getBookIndexList(bookId);
    }

    // 2. getAdminBookSearch method test
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
        when(categoryService.getCategoryListByBookId(1L)).thenReturn(Collections.emptyList());
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
        verify(categoryService).getCategoryListByBookId(1L);
        verify(bookCreatorService).bookCreatorList(1L);
        verify(tagService).getTagName(1L);
        verify(bookTypeService).getUpdateBookTypeList(1L);
        verify(bookIndexService).getBookIndexList(1L);
    }



    // 3.2. Test createBook when the book does not exist (successful creation)
    @Test
    void testCreateBook_Success() throws IOException {
        // Given
        BookCreatDTO bookCreatDTO = BookCreatDTO.builder()
            .title("New Book")
            .isbn("0987654321")
            .categories(Arrays.asList(
                CategoryDTO.builder().name("Fiction").level(1).build(),
                CategoryDTO.builder().name("Thriller").level(2).build()
            ))
            .bookTypes(Arrays.asList(
                BookTypeDTO.builder().type("BOOK").ranks(1).build(),
                BookTypeDTO.builder().type("BESTSELLER").ranks(2).build()
            ))
            .authors(Arrays.asList(
                BookCreatorDTO.builder().name("Author One").role("AUTHOR").build(),
                BookCreatorDTO.builder().name("Author Two").role("EDITOR").build()
            ))
            .publishedDate(LocalDate.of(2023, 1, 1))
            .description("A new exciting book.")
            .regularPrice(2000)
            .salePrice(1500)
            .page(450)
            .stock(80)
            .index("New Book Index")
            .coverImages(Collections.emptyList())
            .detailImages(Collections.emptyList())
            .publisherName("New Publisher")
            .build();

        when(bookService.existsBookByIsbn("0987654321")).thenReturn(false);

        // Mocking book creation
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        when(bookService.createBook(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setTestId(2L); // Simulate saving and setting ID
            return book;
        });

        // Mocking publisher creation
        when(publisherRepository.findByName("New Publisher")).thenReturn(null);
        ArgumentCaptor<Publisher> publisherCaptor = ArgumentCaptor.forClass(Publisher.class);
        when(publisherRepository.save(any(Publisher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        bookMultiTableService.createBook(bookCreatDTO);

        // Then
        verify(bookService).existsBookByIsbn("0987654321");
        verify(bookService).createBook(bookCaptor.capture());

        Book capturedBook = bookCaptor.getValue();
        assertEquals("New Book", capturedBook.getTitle());
        assertEquals("0987654321", capturedBook.getIsbn13());

        verify(publisherRepository).findByName("New Publisher");
        verify(publisherRepository).save(publisherCaptor.capture());

        Publisher capturedPublisher = publisherCaptor.getValue();
        assertEquals("New Publisher", capturedPublisher.getName());

        verify(bookCreatorService, times(2)).saveBookCreator(any(BookCreator.class), any(BookCreatorMap.class));
        verify(bookCategoryRepository, times(2)).save(any(BookCategory.class));

        verify(imageService, never()).bookCoverSave(any(Image.class), any(BookCoverImage.class));
        verify(imageService, never()).bookDetailSave(any(Image.class), any(BookImage.class));
    }

    @Test
    void testCreateBook_Failure() throws IOException {
        String isbn = "0987654321";

        BookCreatDTO creatDTO = BookCreatDTO.builder().isbn(isbn).regularPrice(1).salePrice(1).page(1)
            .stock(1).build();

        when(bookService.existsBookByIsbn(creatDTO.getIsbn())).thenReturn(true);

        assertThrows(BookNotFoundException.class, () -> bookMultiTableService.createBook(creatDTO));

        verify(bookService, times(1)).existsBookByIsbn(isbn);
    }

    @Test
    void testUpdateBook_Success() throws IOException {
        // Arrange
        Book book = mock(Book.class);

        String path = "1245.jpg";
        String fileName = "testFile.txt";
        String contentType = "text/plain";
        String content = "Hello, this is a test file.";

        // MockMultipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",
            fileName,
            contentType,
            content.getBytes()
        );

        BookTypeDTO bookTypeDTO = BookTypeDTO.builder()
            .id(1L)
            .type("BOOK")
            .ranks(0)
            .build();

        BookUpdateDTO bookUpdateDTO = BookUpdateDTO.builder()
            .id(1L)
            .title("New Book")
            .isbn("0987654321")
            .publishedDate(LocalDate.of(2023, 1, 1))
            .description("A new exciting book.")
            .coverImage(List.of(multipartFile))
            .detailImage(List.of(multipartFile))
            .bookTypes(List.of(bookTypeDTO))
            .authors(Collections.emptyList())
            .categories(Collections.emptyList())
            .regularPrice(1)
            .salePrice(1)
            .stock(1)
            .page(1)
            .build();

        when(bookService.getBook(bookUpdateDTO.getId())).thenReturn(book);

        // Mock 설정
        doNothing().when(book).update(
            anyString(),
            anyString(),
            any(LocalDate.class),
            anyInt(),
            anyInt(),
            anyString()
        );

        // Act
        bookMultiTableService.updateBook(bookUpdateDTO);

        // Assert
        verify(imageService, times(1)).bookCoverSave(any(Image.class), any(BookCoverImage.class));
        verify(imageService, times(1)).bookDetailSave(any(Image.class), any(BookImage.class));
        verify(book, times(1)).update(
            eq("New Book"),
            eq("0987654321"),
            eq(LocalDate.of(2023, 1, 1)),
            eq(1),
            eq(1),
            eq("A new exciting book.")
        );
    }






    @Test
    void testBookCoverImageUpdateOrCreate_EmptyCoverImages() throws IOException {
        // Given
        List<MultipartFile> coverImages = Collections.emptyList();
        Book mockBook = Book.builder().id(1L).title("Test Book").isbn13("1234567890").regularPrice(1000)
            .salePrice(1000).stock(100).page(100).build();
        String isbn = "1234567890";

        // When
        bookMultiTableService.bookCoverImageUpdateOrCreate(coverImages, mockBook, isbn);
        bookMultiTableService.bookDetailImageUpdateOrCreate(coverImages, mockBook, isbn);

        // Then
        verify(imageService, never()).getCoverImage(anyLong());
        verify(imageService, never()).bookCoverSave(any(), any());
        verify(imageService, never()).getDetailImage(anyLong());
        verify(imageService, never()).bookDetailSave(any(), any());

    }


    @Test
    void testDeleteBook_service_layer() {
        long bookId = 1L;

        doNothing().when(bookTypeService).deleteBookType(bookId);
        doNothing().when(bookIndexService).deleteBookIndex(bookId);
        doNothing().when(bookCreatorService).deleteBookCreatorMap(bookId);
        doNothing().when(bookCategoryRepository).deleteAllByBookId(bookId);
        doNothing().when(imageService).deleteBookCoverImageAndBookDetailImage(bookId);
        doNothing().when(bookTagService).deleteAllByBookId(bookId);
        doNothing().when(reviewService).deleteAllReviewsWithBook(bookId);

        doNothing().when(bookCouponRepository).deleteByBookId(bookId);
        doNothing().when(wrapperRepository).deleteByBookId(bookId);

        doNothing().when(bookPopularityRepository).deleteByBookId(bookId);
        doNothing().when(bookService).deleteBook(bookId);

        bookMultiTableService.deleteBook(bookId);

        // Then
        // 각 서비스 및 레포지토리가 올바르게 호출되었는지 검증
        verify(bookTypeService, times(1)).deleteBookType(bookId);

        verify(bookCreatorService, times(1)).deleteBookCreatorMap(bookId);
        verify(bookCategoryRepository, times(1)).deleteAllByBookId(bookId);
        verify(imageService, times(1)).deleteBookCoverImageAndBookDetailImage(bookId);
        verify(bookTagService, times(1)).deleteAllByBookId(bookId);
        verify(reviewService, times(1)).deleteAllReviewsWithBook(bookId);
        verify(wrapperRepository, times(1)).deleteByBookId(bookId);



        verify(bookService, times(1)).deleteBook(bookId);
    }


    @Test
    void testBookCoverAndDetailUpdateOrCreate_Update() throws IOException {
        String path = "1245.jpg";
        String fileName = "testFile.txt";
        String contentType = "text/plain";
        String content = "Hello, this is a test file.";

        // MockMultipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",         // 필드 이름
            fileName,       // 파일 이름
            contentType,    // MIME 타입
            content.getBytes() // 파일 내용
        );
        Book book = Book.builder().id(1L).title("Test Book").isbn13("1234567890")
            .regularPrice(1).salePrice(1)
            .stock(1).page(1).build();
        List<MultipartFile> detailImages = List.of(multipartFile);
        Image image = mock(Image.class);

        when(imageService.getCoverImage(book.getId())).thenReturn(image);
        when(imageService.getDetailImage(book.getId())).thenReturn(image);
        when(naverObjectStorageService.uploadFile(book.getIsbn13(), multipartFile)).thenReturn(path);

        bookMultiTableService.bookCoverImageUpdateOrCreate(detailImages, book, book.getIsbn13());
        bookMultiTableService.bookDetailImageUpdateOrCreate(detailImages, book, book.getIsbn13());

        verify(image, times(2)).update(null);

    }

    @Test
    void testBookCoverAndDetailUpdateOrCreate_Create() throws IOException {
        String path = "1245.jpg";
        String fileName = "testFile.txt";
        String contentType = "text/plain";
        String content = "Hello, this is a test file.";

        // MockMultipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",         // 필드 이름
            fileName,       // 파일 이름
            contentType,    // MIME 타입
            content.getBytes() // 파일 내용
        );
        Book book = Book.builder().id(1L).title("Test Book").isbn13("1234567890")
            .regularPrice(1).salePrice(1)
            .stock(1).page(1).build();
        List<MultipartFile> detailImages = List.of(multipartFile);


        when(imageService.getCoverImage(book.getId())).thenReturn(null);
        when(imageService.getDetailImage(book.getId())).thenReturn(null);
        when(naverObjectStorageService.uploadFile(book.getIsbn13(), multipartFile)).thenReturn(path);

        bookMultiTableService.bookCoverImageUpdateOrCreate(detailImages, book, path);
        bookMultiTableService.bookDetailImageUpdateOrCreate(detailImages, book, path);

        verify(imageService, times(1)).bookCoverSave(any(Image.class), any(BookCoverImage.class));
        verify(imageService, times(1)).bookDetailSave(any(Image.class), any(BookImage.class));

    }


    @Test
    void testGetBookOrderDetails_Success() {
        // Arrange
        List<BookOrderRequestDTO> requestDTOList = Arrays.asList(
            new BookOrderRequestDTO(1L, 2),
            new BookOrderRequestDTO(2L, 1)
        );



        List<CategoryDTO> book1Categories = Arrays.asList(new CategoryDTO("Category1"), new CategoryDTO("Category2"));
        List<CategoryDTO> book2Categories = Arrays.asList(new CategoryDTO("Category3"));

        BookOrderDetailResponse book1Detail = BookOrderDetailResponse.builder()
            .id(1L)
            .title("Book 1")
            .stock(10)
            .category(new ArrayList<>())
            .regularPrice(1).salePrice(1).wrappable(true).build();

        BookOrderDetailResponse book2Detail = BookOrderDetailResponse.builder()
            .id(2L)
            .category(new ArrayList<>())
            .title("Book 2")
            .stock(5)
            .regularPrice(1).salePrice(1).wrappable(true).build();

        when(bookRepository.findBookOrderDetail(1L)).thenReturn(book1Detail);
        when(bookRepository.findBookOrderDetail(2L)).thenReturn(book2Detail);
        when(categoryService.getCategoryListByBookId(1L)).thenReturn(book1Categories);
        when(categoryService.getCategoryListByBookId(2L)).thenReturn(book2Categories);


        // Act
        List<BookOrderDetailResponse> result = bookMultiTableService.getBookOrderDetails(requestDTOList);

        // Assert
        assertEquals(2, result.size());

        // Book 1 검증
        BookOrderDetailResponse resultBook1 = result.get(0);
        assertEquals("Book 1", resultBook1.getTitle());
        assertEquals(10, resultBook1.getStock());
        assertEquals(book1Categories, resultBook1.getCategory());

        // Book 2 검증
        BookOrderDetailResponse resultBook2 = result.get(1);
        assertEquals("Book 2", resultBook2.getTitle());
        assertEquals(5, resultBook2.getStock());
        assertEquals(book2Categories, resultBook2.getCategory());

        // Verify 호출 횟수 검증
        verify(bookRepository, times(1)).findBookOrderDetail(1L);
        verify(bookRepository, times(1)).findBookOrderDetail(2L);
        verify(categoryService, times(1)).getCategoryListByBookId(1L);
        verify(categoryService, times(1)).getCategoryListByBookId(2L);
    }


    @Test
    void testGetBookOrderDetail() {
        // Arrange
        long bookId = 1L;
        int quantity = 2;

        BookOrderDetailResponse bookDetailResponse = mock(BookOrderDetailResponse.class);
        when(bookRepository.findBookOrderDetail(bookId)).thenReturn(bookDetailResponse);
        when(bookDetailResponse.getStock()).thenReturn(10);

        List<CategoryDTO> categories = Arrays.asList(mock(CategoryDTO.class), mock(CategoryDTO.class));
        when(categoryService.getCategoryListByBookId(bookId)).thenReturn(categories);

        // Act
        bookMultiTableService.getBookOrderDetail(bookId, quantity);

        // Assert
        verify(bookRepository, times(1)).findBookOrderDetail(bookId);
        verify(bookDetailResponse, times(1)).addCategoryList(categories);
        verify(categoryService, times(1)).getCategoryListByBookId(bookId);
    }

    @Test
    void testGetBookOrderDetails_InsufficientStock() {
        // Arrange
        List<BookOrderRequestDTO> requestDTOList = Arrays.asList(
            new BookOrderRequestDTO(1L, 15)
        );

        BookOrderDetailResponse book1Detail = BookOrderDetailResponse.builder().id(1L)
            .title("Book 1").regularPrice(1000).salePrice(1000).wrappable(true).stock(0).build();

        when(bookRepository.findBookOrderDetail(1L)).thenReturn(book1Detail);

        // Act & Assert
        StockUnavailableException exception = assertThrows(StockUnavailableException.class, () -> {
            bookMultiTableService.getBookOrderDetails(requestDTOList);
        });

        assertEquals("stock not enough", exception.getMessage());
        verify(bookRepository, times(1)).findBookOrderDetail(1L);
        verify(categoryService, never()).getCategoryListByBookId(anyLong());
    }

    @Test
    void testUpdateSearchRank_RetrySuccess() {
        // Arrange
        long bookId = 1L;
        long popularity = 100L;
        BookPopularity bookPopularity = mock(BookPopularity.class);

            doNothing()
            .when(bookPopularityService).updateSearchRank(bookId, popularity);
            when(bookPopularityRepository.findByBookId(bookId)).thenReturn(Optional.of(bookPopularity));

        // Act
        bookMultiTableService.updateSearchRank(bookId, popularity);

        // Assert
        verify(bookPopularityService, times(1)).updateSearchRank(bookId, popularity);
    }


    @Test
    void testBookCoverImageUpdateOrCreate_Success() {
        // Arrange
        String path = "1245.jpg";
        String fileName = "testFile.txt";
        String contentType = "text/plain";
        String content = "Hello, this is a test file.";

        // MockMultipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
            "file",         // 필드 이름
            fileName,       // 파일 이름
            contentType,    // MIME 타입
            content.getBytes() // 파일 내용
        );
        List<MultipartFile> multipartFiles = List.of(multipartFile);
        String isbn = "1341232131";
        Book book = Book.builder().id(1L).isbn13(isbn).regularPrice(1).salePrice(1).stock(1).page(1).build();
        String objectPath = "test/path/to/file.jpg";

        // Mock 서비스 동작 설정
        when(naverObjectStorageService.uploadFile(anyString(), any(MultipartFile.class)))
            .thenReturn(objectPath);

        ArgumentCaptor<Image> imageCaptor = ArgumentCaptor.forClass(Image.class);
        ArgumentCaptor<BookCoverImage> bookCoverImageCaptor = ArgumentCaptor.forClass(BookCoverImage.class);

        // Act
        bookMultiTableService.bookCoverImageUpdateOrCreate(multipartFiles, book, isbn);

        // Assert
        verify(imageService, times(1)).bookCoverSave(imageCaptor.capture(), bookCoverImageCaptor.capture());

        Image capturedImage = imageCaptor.getValue();
        BookCoverImage capturedBookCoverImage = bookCoverImageCaptor.getValue();

        // 캡처된 객체 검증
        assertEquals(objectPath, capturedImage.getUrl());
        assertEquals(book, capturedBookCoverImage.getBook());
        assertEquals(capturedImage, capturedBookCoverImage.getImage());
    }


}
