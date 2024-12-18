package com.nhnacademy.bookapi.controller.book_tag;

import com.nhnacademy.bookapi.dto.book_tag.BookTagRequestDTO;
import com.nhnacademy.bookapi.dto.book_tag.BookTagResponseDTO;
import com.nhnacademy.bookapi.service.book_tag.BookTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookTagController {

    private final BookTagService bookTagService;

    @Operation(
            summary = "책-태그 관계 추가",
            description = "책과 태그 간의 관계를 추가합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "책-태그 관계 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 책-태그 관계")
    })
    @PostMapping("/admin/book-tags")
    public ResponseEntity<Void> addBookTag(@RequestBody BookTagRequestDTO bookTagRequestDTO) {
        bookTagService.addBookTag(bookTagRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "책-태그 관계 삭제",
            description = "책과 태그 간의 관계를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책-태그 관계 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "책-태그 관계를 찾을 수 없음")
    })
    @DeleteMapping("/admin/book-tags")
    public ResponseEntity<Void> deleteBookTag(@RequestBody BookTagRequestDTO bookTagRequestDTO) {
        bookTagService.deleteBookTag(bookTagRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "책-태그 관계 업데이트",
            description = "책과 태그 간의 관계를 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책-태그 관계 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "책-태그 관계를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PutMapping("/admin/book-tags")
    public ResponseEntity<Void> updateBookTag(
            @RequestBody BookTagRequestDTO bookTagRequestDTO,
            @RequestParam Long newTagId
    ) {
        bookTagService.updateBookTag(bookTagRequestDTO, newTagId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "책의 태그 조회",
            description = "특정 책에 연결된 모든 태그를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책의 태그 조회 성공"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @GetMapping("/book-tags/{bookId}")
    public ResponseEntity<List<BookTagResponseDTO>> getBookTagsByBook(@PathVariable Long bookId) {
        List<BookTagResponseDTO> tags = bookTagService.getBookTagsByBook(bookId);
        return ResponseEntity.ok(tags);
    }
}
