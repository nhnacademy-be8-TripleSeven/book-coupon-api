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

import com.nhnacademy.bookapi.dto.category.CategorySearchDTO;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategorySearchDTO> searchCategoriesByName(String query) {
        List<Category> categories = categoryRepository.findByNameContaining(query);
        return categories.stream()
                .map(category -> new CategorySearchDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

}

