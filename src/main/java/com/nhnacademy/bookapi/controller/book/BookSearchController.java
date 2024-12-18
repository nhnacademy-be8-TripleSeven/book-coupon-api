//package com.nhnacademy.bookapi.controller.book;
//
//import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
//import com.nhnacademy.bookapi.service.book.BookService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/search")
//@RequiredArgsConstructor
//public class BookSearchController {
//
//
//    private final BookService bookService;
//
//    @GetMapping("/id")
//    public ResponseEntity<SearchBookDetail> bookTitleSearch(@RequestParam(name = "id") Long id) {
//        return ResponseEntity.ok().body(bookService.searchBookDetailByBookId(id));
//    }
//}
