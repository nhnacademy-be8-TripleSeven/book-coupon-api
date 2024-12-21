package com.nhnacademy.bookapi.controller.book;


import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookMainController {

    private final BookService bookService;

    @Operation(summary = "월간 베스트 조회", description = "메인화면의 월간베스트 책 출력")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "월간 베스트 책 출력 성공"),
        @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @GetMapping("/books/monthly")
    public ResponseEntity<List<BookDetailResponseDTO>> getMonthlyBooks(){
        List<BookDetailResponseDTO> monthlyBestBooks =
            bookService.getMonthlyBestBooks();
        return ResponseEntity.ok(monthlyBestBooks);
    }

    @Operation(summary = "타입별 조회", description = "메인화면의 타입별 책 출력")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "타입별 책 출력 성공"),
        @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @GetMapping("/books/type")
    public ResponseEntity<List<BookDetailResponseDTO>> getBooksByType(@PathVariable String type){
        List<BookDetailResponseDTO> bookTypeBooks = bookService.getBookTypeBooks(
            Type.valueOf(type.toUpperCase()));
        return ResponseEntity.ok(bookTypeBooks);
    }
}
