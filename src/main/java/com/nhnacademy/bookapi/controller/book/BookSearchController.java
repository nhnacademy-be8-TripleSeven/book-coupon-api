package com.nhnacademy.bookapi.controller.book;

import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookSearchResponseDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.book.impl.BookServiceImpl;
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


    @GetMapping("/term/{term}")
    public ResponseEntity<Page<BookDocument>> bookTitleSearch(@PathVariable(name = "term") String term, Pageable pageable) {

        Page<BookDocument> documents = elasticSearchBookSearchRepository.findByTitleContaining(term, pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/typeSearch/{type}")
    public ResponseEntity<Page<BookDetailResponseDTO>> bookTypeSearch(@PathVariable(name = "type") String type, Pageable pageable) {

        Page<BookDetailResponseDTO> bookTypeBooks = bookService.getBookTypeBooks(Type.valueOf(type.toUpperCase()),
            pageable);

        return ResponseEntity.ok(bookTypeBooks);
    }
}
