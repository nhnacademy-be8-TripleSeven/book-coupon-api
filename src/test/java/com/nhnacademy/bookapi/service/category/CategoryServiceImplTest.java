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
import com.nhnacademy.bookapi.dto.category.CategoryLevelDTO;
import com.nhnacademy.bookapi.dto.category.CategoryResponseDTO;
import com.nhnacademy.bookapi.dto.category.CategorySearchDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    void testSaveCategory_service_layer() {
        // Given
        Long categoryId = 1L;
        Category category = Category.builder()
            .id(categoryId)
            .name("Old Category Name")
            .parent(null)
            .level(1)
            .build();

        CategoryDTO categoryDTO = CategoryDTO.builder()
            .id(categoryId)
            .name("New Category Name")
            .level(1)
            .parentCategoryId(categoryId)
            .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When
        categoryService.saveCategory(categoryDTO);

        // Then
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));

        assertEquals(1, category.getLevel()); // 레벨이 변경되지 않았는지 확인
    }

    @Test
    void testCategorySaveList() {
        // Given
        List<CategoryDTO> categoryDTOList = List.of(
            CategoryDTO.builder().name("Category 1").level(1).build(),
            CategoryDTO.builder().name("Category 2").level(2).build()
        );

        // When
        categoryService.categorySaveList(categoryDTOList);

        // Then
        verify(categoryRepository, times(categoryDTOList.size())).save(any(Category.class));
    }



    @Test
    void testGetCategoryByLevel() {
        // Given
        int level = 1;
        Pageable pageable = PageRequest.of(0, 10);

        List<Category> categories = List.of(
            Category.builder().id(1L).name("Category 1").level(level).build(),
            Category.builder().id(2L).name("Category 2").level(level).build()
        );

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        when(categoryRepository.findCategoryByLevel(level, pageable)).thenReturn(categoryPage);

        // When
        Page<CategoryDTO> result = categoryService.getCategoryByLevel(level, pageable);

        // Then
        assertNotNull(result); // 반환된 결과가 null이 아닌지 확인
        assertEquals(2, result.getContent().size()); // 반환된 리스트 크기 확인
        assertEquals("Category 1", result.getContent().get(0).getName()); // 첫 번째 항목의 이름 확인
        assertEquals("Category 2", result.getContent().get(1).getName()); // 두 번째 항목의 이름 확인
        verify(categoryRepository, times(1)).findCategoryByLevel(level, pageable); // 레포지토리 호출 검증
    }

    @Test
    void testSearchCategoriesByName() {
        // Given
        String query = "Test";
        List<Category> categories = List.of(
            Category.builder().id(1L).name("Test Category 1").level(1).build(),
            Category.builder().id(2L).name("Test Category 2").level(1).build()
        );

        when(categoryRepository.findByNameContaining(query)).thenReturn(categories);

        // When
        List<CategorySearchDTO> result = categoryService.searchCategoriesByName(query);

        // Then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(2, result.size()); // 반환된 리스트 크기 확인
        assertEquals("Test Category 1", result.get(0).getName()); // 첫 번째 항목 이름 확인
        assertEquals("Test Category 2", result.get(1).getName()); // 두 번째 항목 이름 확인
        verify(categoryRepository, times(1)).findByNameContaining(query); // 레포지토리 호출 검증
    }

    @Test
    void testGetAllCategoriesAsTree() {
        // Given
        Category childCategory = Category.builder()
            .id(2L)
            .name("Child Category")
            .parent(null)
            .level(2)
            .build();

        Category rootCategory = Category.builder()
            .id(1L)
            .name("Root Category")
            .parent(null)
            .children(List.of(childCategory))
            .level(1)
            .children(List.of(childCategory))
            .build();

        when(categoryRepository.findAllRootCategories()).thenReturn(List.of(rootCategory));

        // When
        List<CategoryResponseDTO> result = categoryService.getAllCategoriesAsTree();

        // Then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(1, result.size()); // 루트 카테고리 개수 확인
        assertEquals("Root Category", result.get(0).getName()); // 루트 카테고리 이름 확인
        assertEquals(1, result.get(0).getChildren().size()); // 자식 카테고리 개수 확인
        assertEquals("Child Category", result.get(0).getChildren().get(0).getName()); // 자식 카테고리 이름 확인
        assertEquals(2, result.get(0).getChildren().get(0).getLevel()); // 자식 카테고리 레벨 확인

        verify(categoryRepository, times(1)).findAllRootCategories(); // 레포지토리 호출 검증
    }

    @Test
    void testGetCategoryLevelList() {
        // Given
        List<Category> categories = List.of(
            Category.builder().id(1L).name("Category 1").level(1).build(),
            Category.builder().id(2L).name("Category 2").level(1).build()
        );

        when(categoryRepository.findByLevel(1)).thenReturn(categories);

        // When
        CategoryLevelDTO result = categoryService.getCategoryLevelList();

        // Then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertNotNull(result.getLevel1()); // 카테고리 리스트가 null이 아닌지 확인
        assertEquals(2, result.getLevel1().size()); // 반환된 카테고리 개수 확인
        assertEquals("Category 1", result.getLevel1().get(0).getName()); // 첫 번째 카테고리 이름 확인
        assertEquals("Category 2", result.getLevel1().get(1).getName()); // 두 번째 카테고리 이름 확인
        verify(categoryRepository, times(1)).findByLevel(1); // 레포지토리 호출 검증
    }

    @Test
    void testGetCategoriesByParentAndLevel() {
        // Given
        Long parentId = 1L;
        int level = 2;

        List<Category> categories = List.of(
            Category.builder().id(1L).name("Child Category 1").level(2).build(),
            Category.builder().id(2L).name("Child Category 2").level(2).build()
        );

        when(categoryRepository.findByParentIdAndLevel(parentId, level)).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.getCategoriesByParentAndLevel(parentId, level);

        // Then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(2, result.size()); // 반환된 카테고리 개수 확인
        assertEquals("Child Category 1", result.get(0).getName()); // 첫 번째 카테고리 이름 확인
        assertEquals(2, result.get(0).getLevel()); // 첫 번째 카테고리 레벨 확인
        assertEquals("Child Category 2", result.get(1).getName()); // 두 번째 카테고리 이름 확인
        assertEquals(2, result.get(1).getLevel()); // 두 번째 카테고리 레벨 확인
        verify(categoryRepository, times(1)).findByParentIdAndLevel(parentId, level); // 레포지토리 호출 검증
    }


}
