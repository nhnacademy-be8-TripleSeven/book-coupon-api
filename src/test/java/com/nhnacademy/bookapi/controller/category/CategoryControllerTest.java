package com.nhnacademy.bookapi.controller.category;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.dto.category.CategoryLevelDTO;
import com.nhnacademy.bookapi.dto.category.CategoryResponseDTO;
import com.nhnacademy.bookapi.service.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCategory() {
        // Arrange
        CategoryDTO mockCategory = new CategoryDTO(1L, "Test Category", 1);

        // Act
        ResponseEntity<Void> response = categoryController.saveCategory(mockCategory);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(categoryService, times(1)).saveCategory(eq(mockCategory));
    }

    @Test
    void testGetCategoryList() {
        // Arrange
        List<CategoryDTO> mockList = Collections.singletonList(new CategoryDTO(1L, "Test Category", 1));
        Page<CategoryDTO> mockPage = new PageImpl<>(mockList, PageRequest.of(0, 10), mockList.size());
        when(categoryService.getCategoryByLevel(anyInt(), any(PageRequest.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<CategoryDTO>> response = categoryController.getCategoryList(1, PageRequest.of(0, 10));

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockPage);
        verify(categoryService, times(1)).getCategoryByLevel(eq(1), any(PageRequest.class));
    }

    @Test
    void testDeleteCategory() {
        // Act
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(categoryService, times(1)).deleteCategoryById(eq(1L));
    }

    @Test
    void testGetCategoryByLevelAsTree() {
        // Arrange
        List<CategoryResponseDTO> mockTree = Arrays.asList(
            new CategoryResponseDTO(1L, "Parent Category")
        );
        when(categoryService.getAllCategoriesAsTree()).thenReturn(mockTree);

        // Act
        ResponseEntity<List<CategoryResponseDTO>> response = categoryController.getCategoryByLevel();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockTree);
        verify(categoryService, times(1)).getAllCategoriesAsTree();
    }

    @Test
    void testGetCategoryLevel() {
        // Arrange
        CategoryLevelDTO mockLevelDTO = new CategoryLevelDTO(Arrays.asList(mock(CategoryDTO.class)), Arrays.asList(mock(CategoryDTO.class)),Arrays.asList(mock(CategoryDTO.class)),Arrays.asList(mock(CategoryDTO.class)),Arrays.asList(mock(CategoryDTO.class)));
        when(categoryService.getCategoryLevelList()).thenReturn(mockLevelDTO);

        // Act
        ResponseEntity<CategoryLevelDTO> response = categoryController.getCategoryLevel();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockLevelDTO);
        verify(categoryService, times(1)).getCategoryLevelList();
    }

    @Test
    void testGetAdminCategoryByLevel() {
        // Arrange
        List<CategoryDTO> mockList = Arrays.asList(new CategoryDTO(1L, "Category1", 1));
        when(categoryService.getCategoryByLevel(anyInt())).thenReturn(mockList);

        // Act
        ResponseEntity<List<CategoryDTO>> response = categoryController.getAdminCategoryByLevel(1);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockList);
        verify(categoryService, times(1)).getCategoryByLevel(eq(1));
    }
}
