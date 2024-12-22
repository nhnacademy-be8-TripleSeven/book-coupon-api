package com.nhnacademy.bookapi.client;

import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "book-api", url = "http://nhn24.store")
public interface BookApiFeignClient {

    @GetMapping("/books/monthly")
    Page<BookDetailResponseDTO> getMonthlyBooks(@RequestParam int page, @RequestParam int size);

    @GetMapping("/books/recommendations")
    Page<BookDetailResponseDTO> getRecommendations(@RequestParam int page, @RequestParam int size);

    @GetMapping("/books/type/{type}")
    Page<BookDetailResponseDTO> getBooksByType(
        @PathVariable("type") String type,
        @RequestParam int page,
        @RequestParam int size
    );
}