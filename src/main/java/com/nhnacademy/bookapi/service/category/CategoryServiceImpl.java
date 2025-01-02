package com.nhnacademy.bookapi.service.category;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> updateCategoryList(Long bookId){
        return categoryRepository.findByBookId(bookId).stream()
            .map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null
            ))
            .collect(Collectors.toList());
    }
}
