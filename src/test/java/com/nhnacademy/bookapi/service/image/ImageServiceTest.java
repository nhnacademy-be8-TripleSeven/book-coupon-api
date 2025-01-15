package com.nhnacademy.bookapi.service.image;



import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.object.ObjectService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;

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
    void testDeleteBookCoverImageAndBookDetailImage_service_layer() {
        // Given
        Long bookId = 1L;
        Book newMockBook = Book.builder()
            .id(bookId) // bookId와 동일하게 설정
            .isbn13("1234567890")
            .publisher(Publisher.builder().id(1L).name("Test").build())
            .regularPrice(1).salePrice(1).stock(1).page(1).build();

        Image mockDetailImage = Image.builder().id(1L).url("detail.jpg").build();
        Image mockCoverImage = Image.builder().id(2L).url("cover.jpg").build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(newMockBook));
        when(bookImageRepository.findImageByBookId(bookId)).thenReturn(List.of(mockDetailImage));
        when(bookCoverImageRepository.findImageByBookId(bookId)).thenReturn(List.of(mockCoverImage));

        doNothing().when(objectService).generateAuthToken();


        doNothing().when(objectService).deleteObject(eq("triple-seven"), anyString());
        doNothing().when(bookImageRepository).deleteByBookId(bookId);
        doNothing().when(bookCoverImageRepository).deleteByBookId(bookId);
        doNothing().when(imageRepository).deleteAll(anyList());

        // When
        imageService.deleteBookCoverImageAndBookDetailImage(bookId);

        // Then
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookImageRepository, times(1)).findImageByBookId(bookId);
        verify(bookImageRepository, times(1)).deleteByBookId(bookId);
        verify(imageRepository, times(1)).deleteAll(List.of(mockDetailImage));
        verify(objectService, times(1)).deleteObject("triple-seven", "1234567890_detail.jpg");

        verify(bookCoverImageRepository, times(1)).findImageByBookId(bookId);
        verify(bookCoverImageRepository, times(1)).deleteByBookId(bookId);
        verify(imageRepository, times(1)).deleteAll(List.of(mockCoverImage));
        verify(objectService, times(1)).deleteObject("triple-seven", "1234567890_cover.jpg");

        verify(objectService, times(1)).generateAuthToken();
    }

    @Test
    void testGetDetailImage_service_layer() {
        // Given
        Long bookId = 1L;
        Image mockImage = Image.builder().id(1L).url("detail.jpg").build();

        // Mock 설정
        when(imageRepository.findDetailImageByBookId(bookId)).thenReturn(Optional.of(mockImage));

        // When
        Image result = imageService.getDetailImage(bookId);

        // Then
        assertNotNull(result); // 반환된 값이 null이 아닌지 확인
        assertEquals(mockImage.getUrl(), result.getUrl()); // 반환된 이미지의 URL 검증
        verify(imageRepository, times(1)).findDetailImageByBookId(bookId); // 메서드 호출 검증
    }




}
