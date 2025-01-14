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
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplierBuilder.Creator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        Book book = Book.builder().title("Test Book").publisher(Publisher.builder().id(1l).name("test").build()).regularPrice(1).salePrice(1).stock(1).page(1).build();
        BookType bookType = BookType.builder().id(1l).types(Type.BOOK).ranks(1).build();
        BookCreator bookCreator = BookCreator.builder().id(1l).name("test").build();
        Category category = Category.builder().id(1l).name("test").level(1).build();

        when(bookTypeRepository.findAllByBook(book)).thenReturn(List.of(bookType));
        when(bookRepository.findBookWithPublisherById(bookId)).thenReturn(Optional.of(book));

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
        List<BookDetailResponseDTO> bookList = new ArrayList<>();
        Page<BookDetailResponseDTO> bookPage = new PageImpl<>(bookList);
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
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        when(bookRepository.findByTitleContaining(query)).thenReturn(books);

        List<BookSearchDTO> result = bookService.searchBooksByName(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findByTitleContaining(query);
    }
}
