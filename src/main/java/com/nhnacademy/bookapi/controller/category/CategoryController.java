package com.nhnacademy.bookapi.controller.category;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.service.category.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<Void> saveCategory(@RequestBody List<CategoryDTO> categoryDTO){
        categoryService.categorySaveList(categoryDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/books/categoryList")
    public ResponseEntity<List<CategoryDTO>> getCategoryList(@RequestParam int level){
        List<CategoryDTO> categoryByLevel = categoryService.getCategoryByLevel(level);

        return ResponseEntity.ok().body(categoryByLevel);
    }

    @DeleteMapping("/admin/books/categoryDelete")
    public ResponseEntity<Void> deleteCategory(@RequestParam long id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }


}
