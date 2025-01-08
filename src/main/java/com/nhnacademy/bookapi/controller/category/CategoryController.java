package com.nhnacademy.bookapi.controller.category;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.service.category.CategoryService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;


    @PostMapping("/admin/books/categoryCreate")
    public ResponseEntity<Void> saveCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.saveCategory(categoryDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/books/categoryList")
    public ResponseEntity<Page<CategoryDTO>> getCategoryList(@RequestParam int level, Pageable pageable){
        Page<CategoryDTO> categoryByLevel = categoryService.getCategoryByLevel(level, pageable);

        return ResponseEntity.ok(categoryByLevel);
    }

    @PostMapping("/admin/books/categoryDelete")
    public ResponseEntity<Void> deleteCategory(@RequestParam(name = "id") Long id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/books/categoryAll")
    public ResponseEntity<List<CategoryDTO>> getAllCategory(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/admin/books/categories")
    public ResponseEntity<List<CategoryDTO>> getCategoryByLevel(@RequestParam int level){
        return ResponseEntity.ok(categoryService.getCategoryByLevel(level));
    }


}
