package com.nhnacademy.bookapi.wrappable_test;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Wrappable;
import com.nhnacademy.bookapi.repository.WrappableRepository;
import com.nhnacademy.bookapi.service.wrappable.WrappableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class WrappableServiceTest {

    @Mock
    private WrappableRepository wrappableRepository;

    @InjectMocks
    private WrappableService wrappableService;

    private Book book;
    private Wrappable wrappable;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        book = new Book();
        wrappable = new Wrappable(book, true);
    }

    @Test
    void addWrappableSuccess() {
        when(wrappableRepository.existsByBook(book)).thenReturn(false);

        boolean result = wrappableService.addWrappable(book, true);

        assertTrue(result);
        verify(wrappableRepository, times(1)).save(any(Wrappable.class));
    }

    @Test
    void addWrappableException() {
        when(wrappableRepository.existsByBook(book)).thenReturn(true);

    }
}
