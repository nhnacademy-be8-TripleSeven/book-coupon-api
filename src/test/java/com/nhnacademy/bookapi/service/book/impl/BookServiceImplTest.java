package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book.*;
import com.nhnacademy.bookapi.dto.page.PageDTO;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCoverImageRepository bookCoverImageRepository;

    @Mock
    private BookCreatorMapRepository bookCreatorMapRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private BookTagRepository bookTagRepository;

    @Mock
    private BookIndexRepository bookIndexRepository;

    @Mock
    private BookTypeRepository bookTypeRepository;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private BookCreatorService bookCreatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBook() {
        Book book = new Book();
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.createBook(book);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDeleteBook_Success() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);

        bookService.deleteBook(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void testDeleteBook_NotFound() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void testSearchBookDetailByBookId_Success() {
        Long bookId = 1L;
        Book book = Book.builder().title("Test Book").isbn13("1234567890123").bookCategories(new ArrayList<>()).publisher(Publisher.builder().id(1L).name("Test Publisher").build()).regularPrice(1).salePrice(1).stock(10).page(200).build();


        when(bookRepository.findBookWithPublisherById(bookId)).thenReturn(Optional.of(book));
        when(bookCoverImageRepository.findByBook(book)).thenReturn(null);
        when(bookCreatorMapRepository.findByBook(book)).thenReturn(Collections.emptyList());
        when(bookCategoryRepository.findAllByBook(book)).thenReturn(Collections.emptyList());
        when(bookTagRepository.findAllByBookWithTags(book)).thenReturn(Collections.emptyList());
        when(bookTypeRepository.findAllByBook(book)).thenReturn(Collections.emptyList());
        when(bookImageRepository.findAllByBookWithImage(book)).thenReturn(Collections.emptyList());


        SearchBookDetail result = bookService.searchBookDetailByBookId(bookId);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository, times(1)).findBookWithPublisherById(bookId);
    }

    @Test
    void testSearchBookDetailByBookId_NotFound() {
        Long bookId = 1L;
        when(bookRepository.findBookWithPublisherById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.searchBookDetailByBookId(bookId));
        verify(bookRepository, times(1)).findBookWithPublisherById(bookId);
    }

    @Test
    void testGetMonthlyBestBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookDetailResponseDTO> bookPage = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findBookTypeBestseller(pageable)).thenReturn(bookPage);

        PageDTO<BookDetailResponseDTO> result = bookService.getMonthlyBestBooks();

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        verify(bookRepository, times(1)).findBookTypeBestseller(pageable);
    }

    @Test
    void testExistsBookByIsbn() {
        String isbn = "1234567890123";
        when(bookRepository.existsByIsbn13(isbn)).thenReturn(true);

        boolean result = bookService.existsBookByIsbn(isbn);

        assertTrue(result);
        verify(bookRepository, times(1)).existsByIsbn13(isbn);
    }

    @Test
    void testGetBookById() {
        Long bookId = 1L;
        BookDTO bookDTO = new BookDTO();
        when(bookRepository.findBookById(bookId)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookById(bookId);

        assertNotNull(result);
        verify(bookRepository, times(1)).findBookById(bookId);
    }

    @Test
    void testSearchBooksByName() {
        String query = "Test";
        List<Book> books = List.of(Book.builder().title("Test Book").regularPrice(1).salePrice(1).stock(1).page(1).build());
        when(bookRepository.findByTitleContaining(query)).thenReturn(books);

        List<BookSearchDTO> result = bookService.searchBooksByName(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByTitleContaining(query);
    }

    @Test
    void testGetCartItemsByIds() {
        List<Long> bookIds = List.of(1L, 2L);
        Book book = Book.builder().id(1L).regularPrice(100).salePrice(80).stock(10).page(1).build();

        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book));

        List<OrderItemDTO> result = bookService.getCartItemsByIds(bookIds);
        List<Book> allById = bookRepository.findAllById(bookIds);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, allById.get(0).getStock());
    }
    @Test
    void testCategoryHierarchyCreation() {
        // Given
        Long bookId = 1L;
        Book book = Book.builder().id(1L).title("test").isbn13("123456678990").publisher(Publisher.builder().id(1l).name("test").build()).regularPrice(1).salePrice(1).stock(1).page(1).build();
        Category parentCategory = Category.builder().name("Parent Category").level(1).build();

        Category childCategory = Category.builder().name("Child Category").parent(parentCategory).level(2).build();

        BookCategory bookCategory = BookCategory.builder().id(1L).category(childCategory).build();

        when(bookRepository.findBookWithPublisherById(bookId)).thenReturn(Optional.of(book));
        when(bookCategoryRepository.findAllByBook(book)).thenReturn(List.of(bookCategory));

        // When
        SearchBookDetail result = bookService.searchBookDetailByBookId(bookId);

        // Then
        assertNotNull(result);
        assertTrue(result.getCategories().toString().contains("Child Category"));
        verify(bookRepository, times(1)).findBookWithPublisherById(bookId);
        verify(bookCategoryRepository, times(1)).findAllByBook(book);
    }

    @Test
    void testGetBookList() {
        // Given
        String keyword = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDTO> bookList = Collections.emptyList();
        Page<BookDTO> page = new PageImpl<>(bookList, pageable, 0);

        when(bookRepository.findBookByKeyword(keyword, pageable)).thenReturn(page);

        // When
        Page<BookDTO> result = bookService.getBookList(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(bookRepository, times(1)).findBookByKeyword(keyword, pageable);
    }

    @Test
    void testGetBook() {
        // Given
        Long bookId = 1L;
        Book mockBook = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        // When
        Book result = bookService.getBook(bookId);

        // Then
        assertNotNull(result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBookList_ServiceLayer() {
        // Given
        String keyword = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDTO> bookList = new ArrayList<>();
        Page<BookDTO> page = new PageImpl<>(bookList, pageable, 0);

        when(bookRepository.findBookByKeyword(keyword, pageable)).thenReturn(page);

        // When
        Page<BookDTO> result = bookService.getBookList(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements()); // 리스트 크기 검증
        verify(bookRepository, times(1)).findBookByKeyword(keyword, pageable);
    }

    @Test
    void testGetBook_ServiceLayer() {
        // Given
        Long bookId = 1L;
        Book mockBook = Book.builder().id(bookId).title("Test Book").regularPrice(1).salePrice(1).stock(1).page(1).build();


        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        // When
        Book result = bookService.getBook(bookId);

        // Then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle()); // 제목 검증
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBookName() {
        // Given
        Long bookId = 1L;
        String bookTitle = "Test Book";

        Book book = Book.builder()
            .id(bookId)
            .title(bookTitle)
            .regularPrice(1)
            .salePrice(1)
            .stock(1)
            .page(1)
            .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        String result = bookService.getBookName(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookTitle, result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void testGetBookName_IsEmpty(){
        String errorMessage = "ERROR: not found book";

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        String bookName = bookService.getBookName(anyLong());

        assertEquals(errorMessage, bookName);
    }

    @Test
    void testSearchByCategoryId_ServiceLayer() {
        // Given
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<BookDetailResponseDTO> bookDetailResponseDTOList = new ArrayList<>();
        PageImpl<BookDetailResponseDTO> page = new PageImpl<>(bookDetailResponseDTOList);

        when(bookRepository.findByCategoryId(categoryId, pageable)).thenReturn(page);

        // When
        Page<BookDetailResponseDTO> result = bookService.searchBookByCategoryId(categoryId, pageable);

        // Then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(0, result.getTotalElements()); // 반환된 리스트의 크기 확인
        verify(bookRepository, times(1)).findByCategoryId(categoryId, pageable);
    }

    @Test
    void testGetBookTypeBooks_ServiceLayer() {
        // Given
        Type bookType = Type.BESTSELLER;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "ranks"));

        List<BookDetailResponseDTO> bookDetailResponseDTOList = new ArrayList<>();
        PageImpl<BookDetailResponseDTO> page = new PageImpl<>(bookDetailResponseDTOList);

        when(bookRepository.findBookTypeItemByType(bookType, pageable)).thenReturn(page);

        // When
        PageDTO<BookDetailResponseDTO> result = bookService.getBookTypeBooks(bookType, pageable);

        // Then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(0, result.getContent().size()); // 리스트 크기 확인
        verify(bookRepository, times(1)).findBookTypeItemByType(bookType, pageable);
    }


}
