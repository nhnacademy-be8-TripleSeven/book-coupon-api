package com.nhnacademy.bookapi.client.service;

import com.nhnacademy.bookapi.client.BookApiFeignClient;
import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BookServiceClient {

    private final BookApiFeignClient bookApiFeignClient;

    public Page<BookDetailResponseDTO> fetchMonthlyBooks(int page, int size) {
        return bookApiFeignClient.getMonthlyBooks(page, size);
    }

    public Page<BookDetailResponseDTO> fetchBooksByType(String type, int page, int size) {
        return bookApiFeignClient.getBooksByType(type, page, size);
    }
}
