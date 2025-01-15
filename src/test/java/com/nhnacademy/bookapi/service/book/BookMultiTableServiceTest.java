package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.BookCreatDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookOrderDetailResponse;
import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.book_index.BookIndexService;
import com.nhnacademy.bookapi.service.book_tag.BookTagService;
import com.nhnacademy.bookapi.service.book_type.BookTypeService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;
import com.nhnacademy.bookapi.service.category.CategoryService;
import com.nhnacademy.bookapi.service.image.ImageService;
import com.nhnacademy.bookapi.service.object.ObjectService;
import com.nhnacademy.bookapi.service.review.ReviewService;
import com.nhnacademy.bookapi.service.tag.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
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
    private BookCouponRepository bookCouponRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;


    @Mock
    private BookTypeRepository bookTypeRepository;

    @Mock
    private BookTagService bookTagService;




    // Constants
    private final String storageUrl = "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e";
    private final String containerName = "triple-seven";

    @BeforeEach
    void setUp() {
        // Initialize any common setup if necessary
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

    // 3. createBook method tests

    // 3.1. Test createBook when the book already exists
//    @Test
//    void testCreateBook_AlreadyExists() throws IOException {
//        // Given
//        BookCreatDTO bookCreatDTO = BookCreatDTO.builder()
//            .title("Existing Book")
//            .isbn("1234567890")
//            .categories(Arrays.asList(CategoryDTO.builder().name("Non-Fiction").level(1).build()))
//            .bookTypes(Arrays.asList(BookTypeDTO.builder().type("PAPERBACK").ranks(2).build()))
//            .authors(Arrays.asList(BookCreatorDTO.builder().name("Existing Author").role("AUTHOR").build()))
//            .publishedDate(LocalDate.of(2022, 5, 15))
//            .description("An existing book.")
//            .regularPrice(1500)
//            .salePrice(1200)
//            .page(400)
//            .stock(60)
//            .index("Existing Index")
//            .coverImages(Collections.emptyList())
//            .detailImages(Collections.emptyList())
//            .publisherName("Existing Publisher")
//            .build();
//
//        when(bookService.existsBookByIsbn("1234567890")).thenReturn(true);
//
//        // When
//        bookMultiTableService.createBook(bookCreatDTO);
//
//        // Then
//        verify(bookService).existsBookByIsbn("1234567890");
//        verify(bookService, never()).createBook(any(Book.class));
//        verify(publisherRepository, never()).findByName(anyString());
//        verify(publisherRepository, never()).save(any(Publisher.class));
//        verify(bookCreatorService, never()).saveBookCreator(any(BookCreator.class), any(BookCreatorMap.class));
//        verify(bookCategoryRepository, never()).save(any(BookCategory.class));
//        verify(bookPopularityRepository, never()).save(any(BookPopularity.class));
//        verify(imageService, never()).bookCoverSave(any(Image.class), any(BookCoverImage.class));
//        verify(imageService, never()).bookDetailSave(any(Image.class), any(BookImage.class));
//    }

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
                BookTypeDTO.builder().type("FICTION").ranks(1).build(),
                BookTypeDTO.builder().type("THRILLER").ranks(2).build()
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



    // 6.1. Test uploadCoverImageToStorage successfully uploads and returns URL
    @Test
    void testUploadCoverImageToStorage_Success() throws IOException {
        // Given
        MultipartFile multipartFile = mock(MultipartFile.class);
        String objectName = "isbn_cover.jpg";
        String expectedUrl = storageUrl + "/" + containerName + "/" + objectName;
        InputStream inputStream = new ByteArrayInputStream("image data".getBytes());
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        // When
        String result = bookMultiTableService.uploadCoverImageToStorage(objectService, multipartFile, objectName);

        // Then
        assertEquals(expectedUrl, result);
        verify(objectService).uploadObject(containerName, objectName, inputStream);
    }

    // 6.2. Test uploadCoverImageToStorage when IOException occurs
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

        verify(objectService, never()).uploadObject(anyString(), anyString(), any());
    }

    // 7. loadImageTOStorage method tests

    // 7.1. Test loadImageTOStorage successfully retrieves the image
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

    // 7.2. Test loadImageTOStorage when the image does not exist (returns null)
    @Test
    void testLoadImageTOStorage_ImageNotFound() {
        // Given
        String objectName = "nonexistent_image.jpg";
        when(objectService.loadImageFromStorage(containerName, objectName)).thenReturn(null);

        // When
        MultipartFile result = bookMultiTableService.loadImageTOStorage(objectService, objectName);

        // Then
        assertNull(result);
        verify(objectService).loadImageFromStorage(containerName, objectName);
    }




    @Test
    void testGetBookOrderDetail_WithEmptyCategoryList() {
        // Given
        long bookId = 1L;
        BookOrderDetailResponse mockBookDetail = BookOrderDetailResponse.builder()
            .id(bookId)
            .title("Test Book")
            .regularPrice(1000)
            .salePrice(1000)
            .wrappable(true)
            .category(new ArrayList<>()) // 명시적으로 초기화
            .build();

        when(bookRepository.findBookOrderDetail(bookId)).thenReturn(mockBookDetail);
        when(categoryService.getCategoryListByBookId(bookId)).thenReturn(Collections.emptyList());

        // When
        BookOrderDetailResponse result = bookMultiTableService.getBookOrderDetail(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Test Book", result.getTitle());
        assertNotNull(result.getCategory());
        assertEquals(0, result.getCategory().size());
        verify(bookRepository, times(1)).findBookOrderDetail(bookId);
        verify(categoryService, times(1)).getCategoryListByBookId(bookId);
    }

    @Test
    void testBookCoverImageUpdateOrCreate_UpdateExistingImage() throws IOException {
        // Given
        List<MultipartFile> coverImages = Collections.singletonList(mock(MultipartFile.class));
        Book mockBook = Book.builder().id(1L).title("Test Book").isbn13("1234567890").regularPrice(1000)
            .salePrice(100).stock(100).page(100).build();
        String isbn = "1234567890";

        Image mockImage = mock(Image.class);
        when(imageService.getCoverImage(mockBook.getId())).thenReturn(mockImage);

        // Mock uploadCoverImageToStorage
        BookMultiTableService spyService = spy(bookMultiTableService);
        String uploadedPath = "uploaded/path.jpg";
        doReturn(uploadedPath).when(spyService).uploadCoverImageToStorage(any(), any(), anyString());

        // When
        spyService.bookCoverImageUpdateOrCreate(coverImages, mockBook, isbn);

        // Then
        verify(imageService, times(1)).getCoverImage(mockBook.getId());
        verify(mockImage, times(1)).update(uploadedPath);
        verify(imageService, never()).bookCoverSave(any(), any());
    }

    @Test
    void testBookCoverImageUpdateOrCreate_CreateNewImage() throws IOException {
        // Given
        List<MultipartFile> coverImages = Collections.singletonList(mock(MultipartFile.class));
        Book mockBook = Book.builder().id(1L).title("Test Book").isbn13("1234567890").regularPrice(1000)
            .salePrice(1000).stock(100).page(100).build();
        String isbn = "1234567890";

        // Mock coverImage가 없는 경우
        when(imageService.getCoverImage(mockBook.getId())).thenReturn(null);

        // Mock uploadCoverImageToStorage
        BookMultiTableService spyService = spy(bookMultiTableService);
        String uploadedPath = "uploaded/cover/path.jpg";
        doReturn(uploadedPath).when(spyService).uploadCoverImageToStorage(any(), any(), anyString());

        // When
        spyService.bookCoverImageUpdateOrCreate(coverImages, mockBook, isbn);

        // Then
        verify(imageService, times(1)).getCoverImage(mockBook.getId());
        verify(imageService, times(1)).bookCoverSave(any(Image.class), any(BookCoverImage.class));
        verifyNoMoreInteractions(imageService);
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

        // Then
        verify(imageService, never()).getCoverImage(anyLong());
        verify(objectService, never()).uploadObject(anyString(), anyString(), any());
        verify(imageService, never()).bookCoverSave(any(), any());
    }

    @Test
    void testBookDetailImageUpdateOrCreate_UpdateExistingImage() throws IOException {
        // Given
        List<MultipartFile> detailImages = Collections.singletonList(mock(MultipartFile.class));
        Book mockBook = Book.builder().id(1L).title("Test Book").isbn13("1234567890").regularPrice(1000)
            .salePrice(1000).stock(100).page(100).build();
        String isbn = "1234567890";

        // Mock detailImage가 이미 존재하는 경우
        Image mockDetailImage = mock(Image.class);
        when(imageService.getDetailImage(mockBook.getId())).thenReturn(mockDetailImage);

        // Mock uploadCoverImageToStorage
        BookMultiTableService spyService = spy(bookMultiTableService);
        String uploadedPath = "uploaded/detail/path.jpg";
        doReturn(uploadedPath).when(spyService).uploadCoverImageToStorage(any(), any(), anyString());

        // When
        spyService.bookDetailImageUpdateOrCreate(detailImages, mockBook, isbn);

        // Then
        verify(imageService, times(1)).getDetailImage(mockBook.getId());
        verify(mockDetailImage, times(1)).update(uploadedPath);
        verify(imageService, never()).bookDetailSave(any(Image.class), any(BookImage.class));
    }

    @Test
    void testBookDetailImageUpdateOrCreate_CreateNewImage() throws IOException {
        // Given
        List<MultipartFile> detailImages = Collections.singletonList(mock(MultipartFile.class));
        Book mockBook = Book.builder().id(1L).title("Test Book").isbn13("1234567890").regularPrice(1000)
            .salePrice(1000).stock(100).page(100).build();
        String isbn = "1234567890";

        Image mockDetailImage = mock(Image.class);

        // Mock detailImage가 없는 경우
        when(imageService.getDetailImage(mockBook.getId())).thenReturn(null);


        // Mock uploadCoverImageToStorage
        BookMultiTableService spyService = spy(bookMultiTableService);
        String uploadedPath = "uploaded/detail/path.jpg";
        doReturn(uploadedPath).when(spyService).uploadCoverImageToStorage(any(), any(), anyString());

        // When
        spyService.bookDetailImageUpdateOrCreate(detailImages, mockBook, isbn);
        verify(imageService, times(1)).getDetailImage(mockBook.getId());
        verify(imageService, times(1)).bookDetailSave(any(Image.class), any(BookImage.class));
        verifyNoMoreInteractions(imageService);
    }

    @Test
    void testUpdateBook_service_layer() throws IOException {
        // Given
        Long bookId = 1L; // Ensure consistent ID
        Book book = Book.builder() // Use spy to allow method calls
            .id(bookId)
            .title("Test Book")
            .isbn13("1234567890")
            .publishDate(LocalDate.now())
            .description("Test Description")
            .regularPrice(1)
            .salePrice(1)
            .stock(1)
            .page(1)
            .build();

        BookTypeDTO bookTypeDTO = BookTypeDTO.builder().ranks(0).type("BOOK").build();
        BookType bookType = BookType.builder().id(1L).ranks(0).types(Type.BESTSELLER).build();

        BookCreatorDTO bookCreatorDTO = BookCreatorDTO.builder().name("test").role("AUTHOR").build();

        List<MultipartFile> bookCoverImages = Collections.singletonList(mock(MultipartFile.class));
        List<MultipartFile> detailImages = Collections.singletonList(mock(MultipartFile.class));
        List<CategoryDTO> categoryDTOS = Collections.singletonList(mock(CategoryDTO.class));
        List<BookCreatorDTO> bookCreatorDTOS = List.of(bookCreatorDTO);
        String index = "testIndex";
        List<BookType> bookTypes = List.of(bookType);
        List<BookTypeDTO> bookTypeDTOS = List.of(bookTypeDTO);

        BookUpdateDTO bookUpdateDTO = BookUpdateDTO.builder()
            .id(bookId)
            .title("Test")
            .isbn("1234567890")
            .index(index)
            .publishedDate(LocalDate.now())
            .regularPrice(1)
            .salePrice(1)
            .description("description")
            .stock(1)
            .page(1)
            .coverImage(bookCoverImages)
            .detailImage(detailImages)
            .bookTypes(bookTypeDTOS)
            .categories(categoryDTOS)
            .authors(bookCreatorDTOS)
            .build();
        // Mock Repository and Service
        when(bookService.getBook(bookId)).thenReturn(book);
        when(bookTypeRepository.findById(anyLong())).thenReturn(Optional.of(mock(BookType.class)));
        when(bookTypeService.getBookTypeByBookId(bookId)).thenReturn(bookTypes);

        // When
        bookMultiTableService.updateBook(bookUpdateDTO);

        // Then
        verify(bookService, times(1)).getBook(bookId);
        verify(bookTypeService, times(1)).getBookTypeByBookId(bookId);

    }

    @Test
    void testDeleteBook_service_layer() {
        long bookId = 1L;

        doNothing().when(bookTypeService).deleteBookType(bookId);
        lenient().doNothing().when(bookIndexService).deleteBookIndex(bookId);
        doNothing().when(bookCreatorService).deleteBookCreatorMap(bookId);
        doNothing().when(bookCategoryRepository).deleteAllByBookId(bookId);
        doNothing().when(imageService).deleteBookCoverImageAndBookDetailImage(bookId);
        doNothing().when(bookTagService).deleteAllByBookId(bookId);
        doNothing().when(reviewService).deleteAllReviewsWithBook(bookId);
        doNothing().when(wrapperRepository).deleteByBookId(bookId);
        doNothing().when(bookCouponRepository).deleteByBookId(bookId);

        lenient().doNothing().when(bookPopularityRepository).deleteByBookId(bookId);
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
        verify(bookCouponRepository, times(1)).deleteByBookId(bookId);


        verify(bookService, times(1)).deleteBook(bookId);
    }







}
