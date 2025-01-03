package com.nhnacademy.bookapi.controller.book;

import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookSearchResponseDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.book.impl.BookServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookSearchController {


    private final BookService bookService;

    private final ElasticSearchBookSearchRepository elasticSearchBookSearchRepository;
    private final BookServiceImpl bookServiceImpl;

    @GetMapping("/id")
    public ResponseEntity<SearchBookDetail> bookTitleSearch(@RequestParam(name = "id") Long id) {
        return ResponseEntity.ok().body(bookService.searchBookDetailByBookId(id));
    }

    @Operation(summary = "도서 검색", description = "검색 기능 제공")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/term/{term}")
    public ResponseEntity<Page<BookDocument>> bookTitleSearch(@PathVariable(name = "term") String term, Pageable pageable) {

        Page<BookDocument> documents = elasticSearchBookSearchRepository.searchWithPopularityAndWeights(term, pageable);
        return ResponseEntity.ok(documents);
    }


    @Operation(summary = "type 검색", description = "타입검색 제공")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/typeSearch/{type}")
    public ResponseEntity<Page<BookDetailResponseDTO>> bookTypeSearch(@PathVariable(name = "type") String type, Pageable pageable) {

        Page<BookDetailResponseDTO> bookTypeBooks = bookService.getBookTypeBooks(Type.valueOf(type.toUpperCase()),
            pageable);


        return ResponseEntity.ok(bookTypeBooks);
    }

    @Operation(summary = "카테고리 별 검색", description = "특정 카테고리 검색 제공")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/categories/{categories}/keyword/{keyword}")
    public ResponseEntity<Page<BookDetailResponseDTO>> bookCategorySearch(@PathVariable(name = "categories") List<String> categories, @PathVariable("keyword") String keyword, Pageable pageable) {
        Page<BookDetailResponseDTO> categorySearchBooks = bookService.getCategorySearchBooks(
            categories, keyword, pageable);
        return ResponseEntity.ok(categorySearchBooks);
    }
}
