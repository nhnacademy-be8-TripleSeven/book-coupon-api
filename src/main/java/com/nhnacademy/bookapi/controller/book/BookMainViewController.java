package com.nhnacademy.bookapi.controller.book;


import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.page.PageDTO;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookMainViewController {

    private final BookService bookService;
    private final BookCreatorService bookCreatorService;


    @GetMapping("/books/monthly")
    public ResponseEntity<List<BookDetailResponseDTO>> getMonthlyBooks() {

        PageDTO<BookDetailResponseDTO> monthlyBestBooks = bookService.getMonthlyBestBooks();
        List<BookDetailResponseDTO> content = monthlyBestBooks.getContent();
        return ResponseEntity.ok(content);
    }

    @GetMapping("/books/recommendations")
    public ResponseEntity<List<BookDetailResponseDTO>> getRecommendations() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "타입별 조회", description = "메인화면의 타입별 책 출력")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "타입별 책 출력 성공"),
        @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @GetMapping("/books/type/{type}")
    public ResponseEntity<List<BookDetailResponseDTO>> getBooksByType(@Valid @PathVariable String type){
        Pageable pageable = PageRequest.of(0, 15);
        PageDTO<BookDetailResponseDTO> bookTypeBooks = bookService.getBookTypeBooks(
            Type.valueOf(type.toUpperCase()), pageable);
        List<BookDetailResponseDTO> content = bookTypeBooks.getContent();
        return ResponseEntity.ok(content);
    }
}
