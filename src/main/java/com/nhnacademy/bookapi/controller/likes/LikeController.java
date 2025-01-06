package com.nhnacademy.bookapi.controller.likes;

import com.nhnacademy.bookapi.dto.likes.LikesRequestDto;
import com.nhnacademy.bookapi.dto.likes.LikesResponseDto;
import com.nhnacademy.bookapi.entity.Likes;
import com.nhnacademy.bookapi.service.likes.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/likes")
//@RequiredArgsConstructor
//public class LikeController {
//
//    private final LikeService likeService;
//
//    @Operation(summary = "좋아요 추가", description = "사용자가 특정 도서에 좋아요를 추가합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "좋아요 추가 성공"),
//            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
//            @ApiResponse(responseCode = "400", description = "이미 좋아요가 존재함")
//    })
//    @PostMapping
//    public ResponseEntity<Void> addLike(@RequestBody LikesRequestDto likesRequestDto) {
//        likeService.addLike(likesRequestDto);
//        return ResponseEntity.status(201).build();
//    }
//
//    @Operation(summary = "좋아요 삭제", description = "사용자가 특정 도서의 좋아요를 삭제합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "좋아요 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "좋아요 또는 도서를 찾을 수 없음")
//    })
//    @DeleteMapping
//    public ResponseEntity<Void> deleteLike(@RequestBody LikesRequestDto likesRequestDto) {
//        likeService.deleteLike(likesRequestDto);
//        return ResponseEntity.noContent().build();
//    }
//
//    @Operation(summary = "유저의 모든 좋아요 조회", description = "사용자가 좋아요를 누른 모든 도서를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자의 좋아요 기록이 없음")
//    })
//    @GetMapping("/{userId}")
//    public ResponseEntity<List<Likes>> getAllLikesByUserId(@PathVariable Long userId) {
//        List<Likes> likes = likeService.getAllLikesByUserId(userId);
//        if (likes.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(likes);
//    }
//}
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 추가", description = "사용자가 특정 도서에 좋아요를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "좋아요 추가 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 좋아요가 존재함")
    })
    @PostMapping("/{bookId}")
    public ResponseEntity<Void> addLike(@RequestHeader("X-USER") Long userId, @PathVariable Long bookId) {
        likeService.addLike(userId, bookId);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "좋아요 삭제", description = "사용자가 특정 도서의 좋아요를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "좋아요 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "좋아요 또는 도서를 찾을 수 없음")
    })
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteLike(@RequestHeader("X-USER") Long userId, @PathVariable Long bookId) {
         likeService.deleteLike(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookId}/status")
    public ResponseEntity<Boolean> checkLikeStatus(@RequestHeader("X-USER") Long userId, @PathVariable Long bookId) {
        boolean isLiked = likeService.isLiked(userId, bookId);
        return ResponseEntity.ok(isLiked);
    }
    
    @Operation(summary = "유저의 모든 좋아요 조회", description = "사용자가 좋아요를 누른 모든 도서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자의 좋아요 기록이 없음")
    })
    @GetMapping
    public ResponseEntity<List<LikesResponseDto>> getAllLikesByUserId(@RequestHeader("X-USER") Long userId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LikesResponseDto> likesPage = likeService.getPagedLikesByUserId(userId, pageable);

        return ResponseEntity.ok(likesPage.getContent());
    }

    @Operation(summary = "유저의 좋아요 내역 조회 (도서 제목 검색 가능)", description = "사용자가 좋아요를 누른 도서를 조회합니다. 도서 제목으로 검색 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "좋아요 기록이 없음")
    })
    @GetMapping("/search")
    public ResponseEntity<List<LikesResponseDto>> searchLikesByUserAndKeyword(
            @RequestHeader("X-USER") Long userId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LikesResponseDto> likesPage = likeService.getPagedLikesByUserIdAndKeyword(userId, keyword, pageable);


        return ResponseEntity.ok(likesPage.getContent());
    }
}
