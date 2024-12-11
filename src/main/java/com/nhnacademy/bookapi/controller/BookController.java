package com.nhnacademy.bookapi.controller;

import com.nhnacademy.bookapi.service.book.BookApiSaveService;
import com.nhnacademy.bookapi.service.book.BookApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/save-book")
public class BookController {

    private final BookApiSaveService bookApiSaveService;


    //bookType = ItemNewAll:신간, Bestseller:베스트셀러
    @GetMapping
    public ResponseEntity saveBook(@RequestParam String bookType) throws Exception {
        bookApiSaveService.saveBook(bookType);

        return ResponseEntity.ok().build();
    }

}
