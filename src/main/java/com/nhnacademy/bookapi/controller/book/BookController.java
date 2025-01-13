package com.nhnacademy.bookapi.controller.book;


import com.nhnacademy.bookapi.dto.book.BookApiDTO;
import com.nhnacademy.bookapi.dto.book.BookCreatDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookOrderDetailResponse;
import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.dto.book.CartItemDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.book_api.BookApiSaveService;
import com.nhnacademy.bookapi.service.book.BookMultiTableService;
import com.nhnacademy.bookapi.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookMultiTableService bookMultiTableService;
    private final BookApiSaveService bookApiSaveService;


    @Operation(summary = "책 생성", description = "새로운 책을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "책 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/admin/books/createBook")
    public ResponseEntity<Void> createBook(@RequestPart BookCreatDTO bookCreatDTO, @RequestPart(value = "coverImages", required = false) List<MultipartFile> coverImages, @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages)
        throws IOException {
        bookCreatDTO.setCoverImages(coverImages);
        bookCreatDTO.setDetailImages(detailImages);
        bookMultiTableService.createBook(bookCreatDTO);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/admin/books/keyword/{keyword}")
    public ResponseEntity<Page<BookDTO>> adminBookList(@PathVariable(name = "keyword") String keyword, Pageable pageable) {
        Page<BookDTO> bookList = bookMultiTableService.getAdminBookSearch(keyword, pageable);
        return ResponseEntity.ok(bookList);
    }

    @Operation(summary = "책 수정", description = "기존 책의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책 수정 성공"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    //ToDo bookUpdate
    @PostMapping("/admin/books/updateBook")
    public ResponseEntity<Void> updateBook(
        @RequestPart("bookUpdateDTO") BookUpdateDTO bookUpdateDTO,
        @RequestPart(value = "coverImage", required = false) List<MultipartFile> coverImages,
        @RequestPart(value = "detailImage", required = false) List<MultipartFile> detailImages
    )
        throws IOException {
        bookUpdateDTO.setCoverImage(coverImages);
        bookUpdateDTO.setDetailImage(detailImages);
        bookMultiTableService.updateBook(bookUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "책 삭제", description = "특정 책을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "책 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음")
    })
    @DeleteMapping("/admin/books/delete")
    public ResponseEntity<Void> deleteBook(@RequestParam("bookId") Long bookId) {
        bookMultiTableService.deleteBook(bookId);
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


    @GetMapping("/admin/books/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        BookDTO adminBookById = bookMultiTableService.getAdminBookById(id);
        return ResponseEntity.ok(adminBookById);
    }

    @GetMapping("/admin/books/isbn/{isbn}")
    public ResponseEntity<BookApiDTO> getBookByIsbn(@PathVariable String isbn) throws Exception {
        BookApiDTO aladinBookByIsbn = bookApiSaveService.getAladinBookByIsbn(isbn);
        return ResponseEntity.ok(aladinBookByIsbn);
    }


    // 오늘 본 상품 조회
    @Operation(summary = "오늘 본 상품 조회", description = "guest Id를 이용하여 오늘 본 상품을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "오늘 본 상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "오늘 본 상품이 없음")
    })
    @GetMapping("/books/today")
    public ResponseEntity<Void> getBookTodayList(@CookieValue("GUEST-ID") String guestId) {
        return ResponseEntity.ok().build();
    }

    // 도서 태그별 조회
    @Operation(summary = "도서 태그별 조회", description = "특정 태그를 가진 도서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도서 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 태그를 가진 도서를 찾을 수 없음")
    })
    @GetMapping("/books/tag/{tag}")
    public ResponseEntity<Void> getBooksByTag(@PathVariable String tag) {
        return ResponseEntity.ok().build();
    }

    // 도서 분류별 조회
    @Operation(summary = "도서 분류별 조회", description = "분류(신간, 베스트셀러 등)에 따른 도서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도서 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 분류를 가진 도서를 찾을 수 없음")
    })
    @GetMapping("/books/booktype/{bookType}")
    public ResponseEntity<Void> getBooksByCategory(@PathVariable String bookType) {
        return ResponseEntity.ok().build();
    }


    @PostMapping("/books/cartItems")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@RequestBody List<Long> bookIds) {
        List<CartItemDTO> cartItemDTOS = bookService.getCartItemsByIds(bookIds);
        return ResponseEntity.ok(cartItemDTOS);
    }

    @GetMapping("/books/{bookId}/name")
    public ResponseEntity<String> getBookName(@PathVariable Long bookId) {
        String name = bookService.getBookName(bookId);
        return ResponseEntity.ok(name);
    }

    @GetMapping("/books/orderDetail/{bookId}")
    public ResponseEntity<BookOrderDetailResponse> getBookOrderDetail(@PathVariable Long bookId) {
        BookOrderDetailResponse bookOrderDetail = bookMultiTableService.getBookOrderDetail(bookId);
        return ResponseEntity.ok(bookOrderDetail);
    }
}
