package com.nhnacademy.bookapi.service.book_tag;

import com.nhnacademy.bookapi.dto.book_tag.BookTagRequestDTO;
import com.nhnacademy.bookapi.dto.book_tag.BookTagResponseDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.BookTagAlreadyExistException;
import com.nhnacademy.bookapi.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTagRepository;
import com.nhnacademy.bookapi.repository.TagRepository;
import com.nhnacademy.bookapi.service.book_tag.BookTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookTagServiceTest {

    @Mock
    private BookTagRepository bookTagRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookTagService bookTagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddBookTagSuccess() {
        Long tagId = 1L;
        Long bookId = 1L;
        BookTagRequestDTO bookTagRequestDTO = new BookTagRequestDTO(tagId, bookId);
        Book book = new Book();
        Tag tag = new Tag();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(false);

        boolean result = bookTagService.addBookTag(bookTagRequestDTO);

        assertTrue(result);
        verify(bookTagRepository, times(1)).save(any(BookTag.class));
    }

    @Test
    void testAddBookTagAlreadyExistException() {
        BookTagRequestDTO bookTagRequestDTO = new BookTagRequestDTO(1L, 1L);
        Book book = new Book();
        Tag tag = new Tag();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(true);

        assertThrows(BookTagAlreadyExistException.class, () -> bookTagService.addBookTag(bookTagRequestDTO));
    }

    @Test
    void testDeleteBookTagSuccess() {
        BookTagRequestDTO bookTagRequestDTO = new BookTagRequestDTO(1L, 1L);
        Book book = new Book();
        Tag tag = new Tag();
        BookTag bookTag = new BookTag(book, tag);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(true);
        when(bookTagRepository.findByBookAndTag(book, tag)).thenReturn(Optional.of(bookTag));

        boolean result = bookTagService.deleteBookTag(bookTagRequestDTO);

        assertTrue(result);
        verify(bookTagRepository, times(1)).delete(bookTag);
    }

    @Test
    void testDeleteBookTagExistNotFoundException() {
        BookTagRequestDTO bookTagRequestDTO = new BookTagRequestDTO(1L, 1L);
        Book book = new Book();
        Tag tag = new Tag();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(false);

        assertThrows(BookTagNotFoundException.class, () -> bookTagService.deleteBookTag(bookTagRequestDTO));
    }

    @Test
    void testDeleteBookTagFindNotFoundException() {
        BookTagRequestDTO bookTagRequestDTO = new BookTagRequestDTO(1L, 1L);
        Book book = new Book();
        Tag tag = new Tag();
        BookTag bookTag = new BookTag(book, tag);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(bookTagRepository.existsByBookAndTag(book, tag)).thenReturn(true);

        when(bookTagRepository.findByBookAndTag(book, tag)).thenReturn(Optional.empty());
        assertThrows(BookTagNotFoundException.class, () -> bookTagService.deleteBookTag(bookTagRequestDTO));
    }

    @Test
    void testDeleteAllByBookIdSuccess() {
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookTagService.deleteAllByBookId(bookId);
        verify(bookTagRepository, times(1)).deleteAllByBook(book);
    }

    @Test
    void getBookTagsByBookSuccess() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        tag1.setId(1L);
        tag1.setName("Tag1");
        tag2.setId(2L);
        tag2.setName("Tag2");

        BookTag bookTag1 = new BookTag(book, tag1);
        BookTag bookTag2 = new BookTag(book, tag2);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookTagRepository.findAllByBookWithTags(book)).thenReturn(Arrays.asList(bookTag1, bookTag2));

        // When
        List<BookTagResponseDTO> result = bookTagService.getBookTagsByBook(bookId);

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getTagId());
        assertEquals("Tag1", result.get(0).getTagName());
        assertEquals(2L, result.get(1).getTagId());
        assertEquals("Tag2", result.get(1).getTagName());
    }

    @Test
    void getBookTagsByBookNotFound() {
        // Given
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookTagService.getBookTagsByBook(bookId));
    }

    @Test
    void testGetTagNotFoundException() {
        Long tagId = 1L;
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> bookTagService.getTag(tagId));
    }
}
