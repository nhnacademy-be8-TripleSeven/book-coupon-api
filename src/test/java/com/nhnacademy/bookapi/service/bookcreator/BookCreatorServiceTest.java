package com.nhnacademy.bookapi.service.bookcreator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorResponseDTO;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.Role;
import com.nhnacademy.bookapi.repository.BookCreatorMapRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookCreatorServiceTest {

    @Mock
    private BookCreatorRepository bookCreatorRepository;

    @Mock
    private BookCreatorMapRepository bookCreatorMapRepository;

    @InjectMocks
    private BookCreatorService bookCreatorService;

    @Test
    void testBookCreatorListByBookId() {
        // Given
        long bookId = 1L;
        List<BookCreator> creators = List.of(
            BookCreator.builder().id(1L).name("Author 1").role(Role.AUTHOR).build(),
            BookCreator.builder().id(2L).name("Illustrator 1").role(Role.ILLUSTRATOR).build()
        );
        when(bookCreatorRepository.findCreatorByBookId(bookId)).thenReturn(creators);

        // When
        BookCreatorResponseDTO result = bookCreatorService.bookCreatorListByBookId(bookId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getCreators().size());
        verify(bookCreatorRepository, times(1)).findCreatorByBookId(bookId);
    }

    @Test
    void testBookCreatorList() {
        // Given
        long bookId = 1L;
        List<BookCreator> creators = List.of(
            BookCreator.builder().id(1L).name("Author 1").role(Role.AUTHOR).build(),
            BookCreator.builder().id(2L).name("Illustrator 1").role(Role.ILLUSTRATOR).build()
        );
        when(bookCreatorRepository.findCreatorByBookId(bookId)).thenReturn(creators);

        // When
        List<BookCreatorDTO> result = bookCreatorService.bookCreatorList(bookId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Author 1", result.get(0).getName());
        verify(bookCreatorRepository, times(1)).findCreatorByBookId(bookId);
    }

    @Test
    void testGetBookCreatorByCreatorId_Found() {
        // Given
        long creatorId = 1L;
        BookCreator creator = BookCreator.builder().id(creatorId).name("Author 1").role(Role.AUTHOR).build();
        when(bookCreatorRepository.findById(creatorId)).thenReturn(Optional.of(creator));

        // When
        BookCreator result = bookCreatorService.getBookCreatorByCreatorId(creatorId);

        // Then
        assertNotNull(result);
        assertEquals("Author 1", result.getName());
        verify(bookCreatorRepository, times(1)).findById(creatorId);
    }

    @Test
    void testGetBookCreatorByCreatorId_NotFound() {
        // Given
        long creatorId = 1L;
        when(bookCreatorRepository.findById(creatorId)).thenReturn(Optional.empty());

        // When
        BookCreator result = bookCreatorService.getBookCreatorByCreatorId(creatorId);

        // Then
        assertNull(result);
        verify(bookCreatorRepository, times(1)).findById(creatorId);
    }

    @Test
    void testSaveBookCreator() {
        // Given
        BookCreator bookCreator = BookCreator.builder().id(1L).name("Author 1").role(Role.AUTHOR).build();
        BookCreatorMap bookCreatorMap = new BookCreatorMap();

        // When
        bookCreatorService.saveBookCreator(bookCreator, bookCreatorMap);

        // Then
        verify(bookCreatorRepository, times(1)).save(bookCreator);
        verify(bookCreatorMapRepository, times(1)).save(bookCreatorMap);
    }

    @Test
    void testSaveBookCreatorMap() {
        // Given
        BookCreatorMap bookCreatorMap = new BookCreatorMap();

        // When
        bookCreatorService.saveBookCreatorMap(bookCreatorMap);

        // Then
        verify(bookCreatorMapRepository, times(1)).save(bookCreatorMap);
    }

    @Test
    void testDeleteBookCreatorMap() {
        // Given
        long bookId = 1L;

        // When
        bookCreatorService.deleteBookCreatorMap(bookId);

        // Then
        verify(bookCreatorMapRepository, times(1)).deleteAllByBookId(bookId);
    }

    @Test
    void testGetBookCreatorByName_Found() {
        // Given
        String name = "Author 1";
        BookCreator creator = BookCreator.builder().id(1L).name(name).role(Role.AUTHOR).build();
        when(bookCreatorRepository.findCreatorByName(name)).thenReturn(Optional.of(creator));

        // When
        BookCreator result = bookCreatorService.getBookCreatorByName(name);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(bookCreatorRepository, times(1)).findCreatorByName(name);
    }

    @Test
    void testGetBookCreatorByName_NotFound() {
        // Given
        String name = "Author 1";
        when(bookCreatorRepository.findCreatorByName(name)).thenReturn(Optional.empty());

        // When
        BookCreator result = bookCreatorService.getBookCreatorByName(name);

        // Then
        assertNull(result);
        verify(bookCreatorRepository, times(1)).findCreatorByName(name);
    }
}
