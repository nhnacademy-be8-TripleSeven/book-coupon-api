package com.nhnacademy.bookapi.controller.book;

import com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.service.book.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {


    private final BookService bookService;

    @GetMapping("/title")
    public ResponseEntity<SearchBookDetailDTO> bookTitleSearch(@RequestParam(name = "id") Long id) {

        SearchBookDetailDTO searchBookDetailDTO = bookService.searchBookDetailByBookId(id);
        if(searchBookDetailDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(searchBookDetailDTO);
    }
}
