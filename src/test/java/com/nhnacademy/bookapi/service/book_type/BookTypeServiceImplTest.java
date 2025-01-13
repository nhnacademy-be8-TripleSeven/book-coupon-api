package com.nhnacademy.bookapi.service.book_type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.repository.BookTypeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookTypeServiceImplTest {

    @Mock
    private BookTypeRepository bookTypeRepository;

    @InjectMocks
    private BookTypeServiceImpl bookTypeService;



    @Test
    void testGetBookTypeByBookId() {
        // Given
        long bookId = 1L;
        List<BookType> mockBookTypes = List.of(new BookType(1L, Type.BOOK, 1,
            Book.builder().id(1l).regularPrice(1).salePrice(1).stock(1).page(1).build()));
        when(bookTypeRepository.findByBookId(bookId)).thenReturn(mockBookTypes);

        // When
        List<BookType> result = bookTypeService.getBookTypeByBookId(bookId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookTypeRepository, times(1)).findByBookId(bookId);
    }

    @Test
    void testDeleteBookType() {
        // Given
        long bookId = 1L;

        // When
        bookTypeService.deleteBookType(bookId);

        // Then
        verify(bookTypeRepository, times(1)).deleteAllByBookId(bookId);
    }

    @Test
    void testCreateBookType() {
        // Given
        BookType mockBookType = new BookType(1L, Type.BOOK, 1,
            Book.builder().id(1l).regularPrice(1).salePrice(1).stock(1).page(1).build());

        // When
        bookTypeService.createBookType(mockBookType);

        // Then
        verify(bookTypeRepository, times(1)).save(mockBookType);
    }
}
