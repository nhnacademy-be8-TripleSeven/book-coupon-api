package com.nhnacademy.bookapi.controller.review;

import com.nhnacademy.bookapi.dto.review.ReviewRequestDto;
import com.nhnacademy.bookapi.dto.review.ReviewResponseDto;
import com.nhnacademy.bookapi.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 추가", description = "특정 도서에 리뷰를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 추가 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 리뷰가 존재함")
    })
    @PostMapping(value = "/api/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addReview(
            @RequestHeader("X-USER") Long userId,
            @RequestPart("reviewRequestDto") ReviewRequestDto reviewRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        reviewService.addReview(userId, reviewRequestDto, file);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "리뷰 수정", description = "특정 도서에 작성된 리뷰를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "404", description = "도서 또는 리뷰를 찾을 수 없음")
    })
    @PutMapping(value = "/api/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateReview(
            @RequestHeader("X-USER") Long userId,
            @RequestPart("reviewRequestDto") ReviewRequestDto reviewRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "removeImage", required = false, defaultValue = "false") Boolean removeImage
    ) {
        // removeImage가 null이면 기본값 false 할당
        boolean shouldRemoveImage = (removeImage != null) ? removeImage : false;

        reviewService.updateReview(userId, reviewRequestDto, file, shouldRemoveImage);
        return ResponseEntity.status(201).build();
    }



    @Operation(summary = "유저가 작성한 모든 리뷰 조회", description = "특정 사용자가 작성한 모든 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping("/api/reviews/all")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsByUserId(@RequestHeader("X-User") Long userId) {
        List<ReviewResponseDto> reviews = reviewService.getAllReviewsByUserId(userId);
        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "특정 도서별 내 리뷰", description = "특정 도서에 대한 내 리뷰 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping("/api/reviews/{bookId}/user")
    public ResponseEntity<ReviewResponseDto> getMyReview(@PathVariable Long bookId, @RequestHeader("X-User") Long userId) {
        ReviewResponseDto reviewResponseDto = reviewService.getReview(bookId, userId);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @Operation(summary = "도서별 전체 페이징 처리 리뷰", description = "특정 도서의 전체 페이징 리뷰 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping("/api/reviews/{bookId}/paged")
    public ResponseEntity<Page<ReviewResponseDto>> getPagedReviewsByBookId(@PathVariable Long bookId, Pageable pageable) {
        Page<ReviewResponseDto> reviewResponseDtos = reviewService.getPagedReviewsByBookId(bookId, pageable);
        return ResponseEntity.ok(reviewResponseDtos);
    }

    @Operation(summary = "도서별 전체 리뷰", description = "특정 도서의 전체 리뷰 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping("/api/reviews/{bookId}/all")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsByBookId(@PathVariable Long bookId) {
        List<ReviewResponseDto> reviews = reviewService.getAllReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }
}
