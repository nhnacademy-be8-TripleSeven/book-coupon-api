package com.nhnacademy.bookapi.controller.book;

import com.nhnacademy.bookapi.dto.book.CreateBookRequest;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.book.UpdateBookRequest;
import com.nhnacademy.bookapi.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "책 생성", description = "새로운 책을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "책 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/admin/books")
    public ResponseEntity<Void> createBook(@RequestBody CreateBookRequest request) {
        bookService.createBook(request);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "책 수정", description = "기존 책의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책 수정 성공"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @PutMapping("/admin/books")
    public ResponseEntity<Void> updateBook(@RequestBody UpdateBookRequest request) {
        bookService.update(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "책 삭제", description = "특정 책을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "책 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @DeleteMapping("/admin/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.delete(bookId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "책 상세 조회", description = "책 ID를 사용하여 책의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책 조회 성공"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @GetMapping("/books/{bookId}")
    public ResponseEntity<SearchBookDetail> getBookDetail(@PathVariable Long bookId) {
        SearchBookDetail searchBookDetail = bookService.searchBookDetailByBookId(bookId);
        return ResponseEntity.ok(searchBookDetail);
    }
}
