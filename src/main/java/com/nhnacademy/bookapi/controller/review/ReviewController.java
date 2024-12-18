package com.nhnacademy.bookapi.controller.review;

import com.nhnacademy.bookapi.dto.review.ReviewRequestDto;
import com.nhnacademy.bookapi.entity.Review;
import com.nhnacademy.bookapi.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//
//@RestController
//@RequestMapping("/api/reviews")
//@RequiredArgsConstructor
//public class ReviewController {
//
//    private final ReviewService reviewService;
//
//    @Operation(summary = "리뷰 추가", description = "특정 도서에 리뷰를 추가합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "리뷰 추가 성공"),
//            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
//            @ApiResponse(responseCode = "400", description = "이미 리뷰가 존재함")
//    })
//    @PostMapping
//    public ResponseEntity<Void> addReview(@RequestBody ReviewRequestDto reviewRequestDto) {
//        reviewService.addReview(reviewRequestDto);
//        return ResponseEntity.status(201).build();
//    }
//
//    @Operation(summary = "리뷰 수정", description = "특정 도서에 작성된 리뷰를 수정합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
//            @ApiResponse(responseCode = "404", description = "도서 또는 리뷰를 찾을 수 없음")
//    })
//    @PutMapping
//    public ResponseEntity<Void> updateReview(@RequestBody ReviewRequestDto reviewRequestDto) {
//        reviewService.updateReview(reviewRequestDto);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(summary = "리뷰 삭제", description = "특정 도서에 작성된 리뷰를 삭제합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "도서 또는 리뷰를 찾을 수 없음")
//    })
//    @DeleteMapping
//    public ResponseEntity<Void> deleteReview(@RequestBody ReviewRequestDto reviewRequestDto) {
//        reviewService.deleteReview(reviewRequestDto);
//        return ResponseEntity.noContent().build();
//    }
//
//    @Operation(summary = "유저가 작성한 모든 리뷰 조회", description = "특정 사용자가 작성한 모든 리뷰를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
//            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
//    })
//    @GetMapping("/{userId}")
//    public ResponseEntity<List<Review>> getAllReviewsByUserId(@PathVariable Long userId) {
//        List<Review> reviews = reviewService.getAllReviewsByUserId(userId);
//        if (reviews.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(reviews);
//    }
//}
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 추가", description = "특정 도서에 리뷰를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 추가 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 리뷰가 존재함")
    })
    @PostMapping
    public ResponseEntity<Void> addReview(@RequestHeader("X-User") Long userId, @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.addReview(userId, reviewRequestDto);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "리뷰 수정", description = "특정 도서에 작성된 리뷰를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "404", description = "도서 또는 리뷰를 찾을 수 없음")
    })
    @PutMapping
    public ResponseEntity<Void> updateReview(@RequestHeader("X-User") Long userId, @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.updateReview(userId, reviewRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 삭제", description = "특정 도서에 작성된 리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서 또는 리뷰를 찾을 수 없음")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@RequestHeader("X-User") Long userId, @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.deleteReview(userId, reviewRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "유저가 작성한 모든 리뷰 조회", description = "특정 사용자가 작성한 모든 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviewsByUserId(@RequestHeader("X-User") Long userId) {
        List<Review> reviews = reviewService.getAllReviewsByUserId(userId);
        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviews);
    }
}
