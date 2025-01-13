//package com.nhnacademy.bookapi.controller.book_index;
//
//import com.nhnacademy.bookapi.dto.book_index.BookIndexRequestDto;
//import com.nhnacademy.bookapi.dto.book_index.BookIndexResponseDto;
//import com.nhnacademy.bookapi.service.book_index.BookIndexService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
////목차 수정
//
//@RestController
//@RequiredArgsConstructor
//public class BookIndexController {
//
//
//    private final BookIndexService bookIndexService;
//
//    @Operation(summary = "책 목차 생성", description = "특정 책에 새로운 목차를 추가합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "목차 생성 성공"),
//            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음"),
//            @ApiResponse(responseCode = "400", description = "목차가 이미 존재함")
//    })
//    @PostMapping("/admin/book-indices")
//    public ResponseEntity<Void> addIndex(@RequestBody BookIndexRequestDto requestDto) {
//        bookIndexService.addIndex(requestDto);
//        return ResponseEntity.status(201).build();
//    }
//
//    @Operation(summary = "책의 모든 목차 조회", description = "특정 책의 모든 목차를 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "목차 조회 성공"),
//            @ApiResponse(responseCode = "404", description = "책 또는 목차를 찾을 수 없음")
//    })
//    @GetMapping("/book-indices/{bookId}")
//    public ResponseEntity<List<BookIndexResponseDto>> getIndicesByBook(@PathVariable Long bookId) {
//        //Todo-1 bookindex
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(summary = "책 목차 수정", description = "특정 책의 목차를 수정합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "목차 수정 성공"),
//            @ApiResponse(responseCode = "404", description = "책 또는 목차를 찾을 수 없음")
//    })
//    @PutMapping("/admin/book-indices")
//    public ResponseEntity<Void> updateIndex(@RequestBody BookIndexRequestDto requestDto) {
//        bookIndexService.updateIndex(requestDto);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(summary = "책 목차 삭제", description = "특정 목차를 삭제합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "목차 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "목차를 찾을 수 없음")
//    })
//    @DeleteMapping("/admin/book-indices/{bookId}")
//    public ResponseEntity<Void> deleteIndex(@PathVariable Long bookId, @RequestParam int sequence) {
//        bookIndexService.deleteIndex(bookId);
//        return ResponseEntity.noContent().build();
//    }
//}
