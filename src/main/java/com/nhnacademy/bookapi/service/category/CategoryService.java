package com.nhnacademy.bookapi.service.category;


import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.dto.category.CategoryLevelDTO;
import com.nhnacademy.bookapi.dto.category.CategoryResponseDTO;
import com.nhnacademy.bookapi.dto.category.CategorySearchDTO;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDTO> updateCategoryList(Long bookId);

    Category getCategoryById(Long categoryId);

    void categorySave(Category category, BookCategory bookCategory);

    Category getCategoryByName(String categoryName);

    void bookCategorySave(BookCategory bookCategory);

    void saveCategory(CategoryDTO categoryDTO);

    void categorySaveList(List<CategoryDTO> categoryDTOList);

    Page<CategoryDTO> getCategoryByLevel(int level, Pageable pageable);

    void deleteCategoryById(Long categoryId);


    List<CategorySearchDTO> searchCategoriesByName(String name);

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getCategoryByLevel(int level);

    List<CategoryResponseDTO> getAllCategoriesAsTree();

    CategoryLevelDTO getCategoryLevelList();

    List<CategoryDTO> getCategoryListByBookId(Long bookId);


}



