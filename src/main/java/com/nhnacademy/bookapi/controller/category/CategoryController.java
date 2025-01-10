package com.nhnacademy.bookapi.controller.category;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.dto.category.CategoryLevelDTO;
import com.nhnacademy.bookapi.dto.category.CategoryResponseDTO;
import com.nhnacademy.bookapi.service.category.CategoryService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    public ResponseEntity<Void> deleteCategory(@RequestParam(name = "id", required = false) Long id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/admin/books/categories/tree")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoryByLevel(){

        List<CategoryResponseDTO> allCategoriesAsTree = categoryService.getAllCategoriesAsTree();

        return ResponseEntity.ok(allCategoriesAsTree);

    }

    @GetMapping("/admin/books/categoryLevelList")
    public ResponseEntity<CategoryLevelDTO> getCategoryLevel(){
        CategoryLevelDTO categoryLevelList = categoryService.getCategoryLevelList();
        return ResponseEntity.ok(categoryLevelList);
    }

    @GetMapping("/admin/books/categories")
    public ResponseEntity<List<CategoryDTO>> getAdminCategoryByLevel(int level){
        List<CategoryDTO> categoryByLevel = categoryService.getCategoryByLevel(level);
        return ResponseEntity.ok(categoryByLevel);
    }






}
