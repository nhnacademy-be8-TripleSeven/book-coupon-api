package com.nhnacademy.bookapi.service.category;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.Category;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> updateCategoryList(Long bookId);

    Category getCategoryById(Long categoryId);

    void categorySave(Category category, BookCategory bookCategory);

    Category getCategoryByName(String categoryName);

    void bookCategorySave(BookCategory bookCategory);

    void saveCategory(CategoryDTO categoryDTO);

    void categorySaveList(List<CategoryDTO> categoryDTOList);

    List<CategoryDTO> getCategoryByLevel(int level);

    void deleteCategoryById(Long categoryId);
}
