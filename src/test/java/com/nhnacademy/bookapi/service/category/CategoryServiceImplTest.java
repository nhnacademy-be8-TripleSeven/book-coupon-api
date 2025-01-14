package com.nhnacademy.bookapi.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testGetCategoryList_ByBookId_WithCategories() {
        // Given
        Long bookId = 1L;
        List<Category> categories = List.of(
            Category.builder().id(1L).name("Category 1").level(1).build(),
            Category.builder().id(2L).name("Category 2").level(2).build()
        );
        when(categoryRepository.findByBookId(bookId)).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.getCategoryListByBookId(bookId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        verify(categoryRepository, times(1)).findByBookId(bookId);
    }

    @Test
    void testGetCategoryById_Found() {
        // Given
        Long categoryId = 1L;
        Category category = Category.builder().id(1L).name("Category 1").parent(null).level(1).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        Category result = categoryService.getCategoryById(categoryId);

        // Then
        assertNotNull(result);
        assertEquals("Category 1", result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryById_NotFound() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Category result = categoryService.getCategoryById(categoryId);

        // Then
        assertNull(result);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testCategorySave() {
        // Given
        Category category = Category.builder().id(1L).name("Category 1").level(1).build();
        Book book = Book.builder().id(1L).title("Test Book").regularPrice(1000).salePrice(1000).stock(100)
            .page(1000).build();
        BookCategory bookCategory = BookCategory.builder().book(book).category(category).id(1l)
            .build();

        // When
        categoryService.categorySave(category, bookCategory);

        // Then
        verify(categoryRepository, times(1)).save(category);
        verify(bookCategoryRepository, times(1)).save(bookCategory);
    }

    @Test
    void testDeleteCategoryById_CategoryExists() {
        // Given
        Long categoryId = 1L;
        Category category = Category.builder().id(1L).name("Category 1").parent(null).level(1).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        categoryService.deleteCategoryById(categoryId);

        // Then
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategoryById_CategoryNotExists() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        categoryService.deleteCategoryById(categoryId);

        // Then
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void testGetAllCategories() {
        // Given
        List<Category> categories = List.of(
            Category.builder().id(1L).name("Root 1").parent(null).level(1).build(),
            Category.builder().id(2L).name("Child 1").parent(null).level(1).build()
        );
        when(categoryRepository.findAllRootCategories()).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAllRootCategories();
    }

    @Test
    void testGetCategoryByLevel_Found() {
        // Given
        int level = 1;
        List<Category> categories = List.of(Category.builder().id(1L).name("Category 1").parent(null).level(1).build());
        when(categoryRepository.findByLevel(level)).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.getCategoryByLevel(level);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Category 1", result.get(0).getName());
        verify(categoryRepository, times(1)).findByLevel(level);
    }
}
