package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorResponseDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCoverImage;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.entity.Publisher;
import com.nhnacademy.bookapi.entity.Role;
import com.nhnacademy.bookapi.entity.Type;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

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

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBook() {
        Book book = new Book();
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.createBook(book);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void deleteBook() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);

        bookService.deleteBook(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBookThrowsExceptionWhenBookNotFound() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));
    }

//    @Test
//    void searchBookDetailByBookId() {
//        Long bookId = 1L;
//
//        Publisher publisher = Publisher.builder()
//            .name("pub")
//            .id(1L)
//            .build();
//        Book book = Book.builder()
//            .id(bookId)
//            .title("Test Book")
//            .description("Description")
//            .regularPrice(1)
//            .publisher(publisher)
//            .salePrice(1)
//            .stock(1)
//            .page(1)
//            .build();
//
//        Image image = Image.builder().id(1l).url("http://example.com/image.jpg").build();
//        BookCoverImage bookCoverImage = BookCoverImage.builder().id(1l).image(image).book(book).build();
//
//        // Mock BookCreator and BookCreatorMap
//        BookCreator bookCreator = mock(BookCreator.class);
//        when(bookCreator.getName()).thenReturn("Test Creator");
//        when(bookCreator.getRole()).thenReturn(Role.AUTHOR); // Example Role
//
//        BookCreatorMap bookCreatorMap = mock(BookCreatorMap.class);
//        when(bookCreatorMap.getCreator()).thenReturn(bookCreator);
//
//
//
//        when(bookRepository.findBookWithPublisherById(bookId)).thenReturn(Optional.of(book));
//        when(bookCoverImageRepository.findByBook(any())).thenReturn(bookCoverImage);
//        when(bookCreatorMapRepository.findByBook(book)).thenReturn(List.of(mock(BookCreatorMap.class)));
//        when(bookCategoryRepository.findAllByBook(book)).thenReturn(List.of(mock(BookCategory.class)));
//        when(bookTagRepository.findAllByBookWithTags(book)).thenReturn(Collections.emptyList());
//        when(bookIndexRepository.findByBook(book)).thenReturn(Optional.empty());
//        when(bookTypeRepository.findAllByBook(book)).thenReturn(List.of(mock(BookType.class)));
//        when(bookImageRepository.findAllByBookWithImage(book)).thenReturn(Collections.emptyList());
//
//        SearchBookDetail result = bookService.searchBookDetailByBookId(bookId);
//
//        assertNotNull(result);
//        assertEquals("Test Book", result.getTitle());
//        assertEquals("http://example.com/image.jpg", result.getCoverUrl());
//        verify(bookRepository, times(1)).findBookWithPublisherById(bookId);
//    }
    @Test
    void existsBookByIsbn() {
        String isbn = "1234567890123";
        when(bookRepository.existsByIsbn13(isbn)).thenReturn(true);

        boolean result = bookService.existsBookByIsbn(isbn);

        assertTrue(result);
        verify(bookRepository, times(1)).existsByIsbn13(isbn);
    }

    @Test
    void getBookById() {
        Long bookId = 1L;
        BookDTO bookDTO = new BookDTO();
        when(bookRepository.findBookById(bookId)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookById(bookId);

        assertNotNull(result);
        verify(bookRepository, times(1)).findBookById(bookId);
    }

    @Test
    void searchBooksByName() {
        String query = "Test";
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        when(bookRepository.findByTitleContaining(query)).thenReturn(books);

        List<?> result = bookService.searchBooksByName(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findByTitleContaining(query);
    }

    @Test
    void getBookName() {
        Long bookId = 1L;
        Book book = Book.builder().title("Test Book").regularPrice(1).salePrice(1).stock(1).page(1)
            .build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        String result = bookService.getBookName(bookId);

        assertEquals("Test Book", result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookNameThrowsErrorWhenNotFound() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        String result = bookService.getBookName(bookId);

        assertEquals("ERROR: not found book", result);
        verify(bookRepository, times(1)).findById(bookId);
    }
}
