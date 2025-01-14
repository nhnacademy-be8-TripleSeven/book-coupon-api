package com.nhnacademy.bookapi.service.category;

import com.nhnacademy.bookapi.dto.book.BookSearchDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.dto.category.CategoryLevelDTO;
import com.nhnacademy.bookapi.dto.category.CategoryResponseDTO;
import com.nhnacademy.bookapi.dto.category.CategorySearchDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final List<CategoryDTO> categoryDTOS = new ArrayList<>();

    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoryListByBookId(Long bookId){
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



    @Transactional
    @Override
    public void saveCategory(CategoryDTO categoryDTO) {
        Category parentCategory = null;
        if(categoryDTO.getParentCategoryId() != null){
            parentCategory = categoryRepository.findById(categoryDTO.getParentCategoryId()).orElse(null);
        }

        Category saveCategory = new Category(categoryDTO.getName(), categoryDTO.getLevel(), parentCategory);
        categoryRepository.save(saveCategory);
    }

    public void categorySaveList(List<CategoryDTO> categoryDTOList) {
        categoryDTOList.forEach(categoryDTO -> categoryRepository.save(new Category(categoryDTO.getName(), categoryDTO.getLevel())));
    }

    //관리자 페이지에서 카테고리 레벨별 조회
    @Transactional(readOnly = true)
    @Override
    public Page<CategoryDTO> getCategoryByLevel(int level, Pageable pageable) {
        Page<Category> categoryByLevel = categoryRepository.findCategoryByLevel(level, pageable);
        List<CategoryDTO> list = categoryByLevel.getContent().stream().map(
                category -> new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    category.getLevel()))
            .toList();
        return new PageImpl<>(list, pageable, categoryByLevel.getTotalElements());
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        Optional<Category> byId = categoryRepository.findById(categoryId);
        byId.ifPresent(categoryRepository::delete);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategorySearchDTO> searchCategoriesByName(String query) {
        List<Category> categories = categoryRepository.findByNameContaining(query);
        return categories.stream()
                .map(category -> new CategorySearchDTO(category.getId(), category.getName()))
                .toList();
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categoryAll = categoryRepository.findAllRootCategories();
        return convertCategoriesToDTOs(categoryAll);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getCategoryByLevel(int level) {
        List<Category> byLevel = categoryRepository.findByLevel(level);
        return convertCategoriesToDTOs(byLevel);
    }

    private List<CategoryDTO> convertCategoriesToDTOs(List<Category> categories) {
        for (Category category : categories) {
            CategoryDTO categoryDTO;
            if (category.getParent() != null) {
                categoryDTO = new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    category.getLevel(),
                    new CategoryDTO(
                        category.getParent().getId(),
                        category.getParent().getName(),
                        category.getParent().getLevel(),
                        null
                    )
                );
            } else {
                categoryDTO = new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    category.getLevel(),
                    null
                );
            }
            categoryDTOS.add(categoryDTO);
        }
        return categoryDTOS;
    }


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "categories", key = "'category:all'")
    public List<CategoryResponseDTO> getAllCategoriesAsTree() {
        List<Category> rootCategories = categoryRepository.findAllRootCategories(); // 루트 카테고리 가져오기
        return buildCategoryTree(rootCategories);
    }

    @Override
    public CategoryLevelDTO getCategoryLevelList() {
        return new CategoryLevelDTO(categoryRepository.findByLevel(1).stream().map(category -> new CategoryDTO(category.getId(), category.getName(), category.getLevel())).toList());
    }

    @Override
    public List<CategoryDTO> getCategoriesByParentAndLevel(Long parentId, int level) {
        return categoryRepository.findByParentIdAndLevel(parentId, level).stream().map(category -> new CategoryDTO(category.getId(), category.getName(), category.getLevel())).toList();
    }

    private List<CategoryResponseDTO> buildCategoryTree(List<Category> categories) {
        List<CategoryResponseDTO> categoryTree = new ArrayList<>();

        for (Category category : categories) {
            CategoryResponseDTO categoryDTO = new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getLevel(),
                buildCategoryTree(category.getChildren()) // 자식 트리 생성
            );
            categoryTree.add(categoryDTO);
        }

        return categoryTree;
    }


}
