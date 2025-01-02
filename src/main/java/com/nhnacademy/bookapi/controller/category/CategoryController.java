package com.nhnacademy.bookapi.controller.category;
import com.nhnacademy.bookapi.dto.category.CategorySearchDTO;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.service.category.CategoryService;
import com.nhnacademy.bookapi.service.coupon.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 쿠폰 대상 지정을 위한 카테고리 검색
    @GetMapping("/admin/search/category")
    public ResponseEntity<List<CategorySearchDTO>> searchCategoriesForCoupon(@RequestParam("query") String query) {
        List<CategorySearchDTO> results = categoryService.searchCategoriesByName(query);
        return ResponseEntity.ok(results);
    }

}
