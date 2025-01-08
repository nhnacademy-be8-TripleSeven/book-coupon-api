package com.nhnacademy.bookapi.service.category;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> updateCategoryList(Long bookId){
        return categoryRepository.findByBookId(bookId).stream()
            .map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getLevel() >= 0 ? category.getLevel() : 5
            )).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    @Override
    public void categorySave(Category category, BookCategory bookCategory) {
        categoryRepository.save(category);
        bookCategoryRepository.save(bookCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName);
    }

    @Override
    public void bookCategorySave(BookCategory bookCategory) {
        bookCategoryRepository.save(bookCategory);
    }

    @Override
    public void saveCategory(CategoryDTO categoryDTO) {
        categoryRepository.save(new Category(categoryDTO.getName(), categoryDTO.getLevel()));
    }

    public void categorySaveList(List<CategoryDTO> categoryDTOList) {
        categoryDTOList.forEach(categoryDTO -> categoryRepository.save(new Category(categoryDTO.getName(), categoryDTO.getLevel())));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getCategoryByLevel(int level) {
        return categoryRepository.findCategoryByLevel(level).stream().map(category -> new CategoryDTO(category.getId(),category.getName(), category.getLevel())).toList();
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        Optional<Category> byId = categoryRepository.findById(categoryId);
        byId.ifPresent(categoryRepository::delete);
    }
}
