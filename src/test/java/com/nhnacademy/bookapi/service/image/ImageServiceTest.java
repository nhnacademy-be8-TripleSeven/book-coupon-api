package com.nhnacademy.bookapi.service.image;



import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.object.ObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private BookCoverImageRepository bookCoverImageRepository;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ObjectService objectService;

    private Book mockBook;
    private Image mockImage;
    private BookCoverImage mockBookCoverImage;

    @BeforeEach
    void setUp() {
        mockBook = Book.builder()
            .id(1L)
            .isbn13("1234567890")
            .regularPrice(1000).salePrice(1000).stock(1000).page(100).build();
        mockImage = new Image("test_path.jpg");
        mockBookCoverImage = new BookCoverImage(mockImage, mockBook);
    }

    @Test
    void testBookCoverSave() {
        // When
        imageService.bookCoverSave(mockImage, mockBookCoverImage);

        // Then
        verify(imageRepository, times(1)).save(mockImage);
        verify(bookCoverImageRepository, times(1)).save(mockBookCoverImage);
    }

    @Test
    void testBookDetailSave() {
        BookImage mockBookImage = new BookImage(mockBook, mockImage);

        // When
        imageService.bookDetailSave(mockImage, mockBookImage);

        // Then
        verify(imageRepository, times(1)).save(mockImage);
        verify(bookImageRepository, times(1)).save(mockBookImage);
    }

    @Test
    void testGetBookCoverImages() {
        // Given
        when(imageRepository.findBookCoverImageByBookId(1L))
            .thenReturn(Arrays.asList("cover1.jpg", "cover2.jpg"));

        // When
        List<String> result = imageService.getBookCoverImages(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("cover1.jpg", result.get(0));
        verify(imageRepository, times(1)).findBookCoverImageByBookId(1L);
    }

    @Test
    void testGetBookDetailImages() {
        // Given
        when(imageRepository.findBookImageByBookId(1L))
            .thenReturn(Collections.singletonList("detail1.jpg"));

        // When
        List<String> result = imageService.getBookDetailImages(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("detail1.jpg", result.get(0));
        verify(imageRepository, times(1)).findBookImageByBookId(1L);
    }

    @Test
    void testGetCoverImage() {
        // Given
        when(imageRepository.findCoverImageByBookId(1L)).thenReturn(Optional.of(mockImage));

        // When
        Image result = imageService.getCoverImage(1L);

        // Then
        assertNotNull(result);
        assertEquals("test_path.jpg", result.getUrl());
        verify(imageRepository, times(1)).findCoverImageByBookId(1L);
    }

    @Test
    void testGetDetailImage() {
        // Given
        when(imageRepository.findDetailImageByBookId(1L)).thenReturn(Optional.of(mockImage));

        // When
        Image result = imageService.getDetailImage(1L);

        // Then
        assertNotNull(result);
        assertEquals("test_path.jpg", result.getUrl());
        verify(imageRepository, times(1)).findDetailImageByBookId(1L);
    }





}
