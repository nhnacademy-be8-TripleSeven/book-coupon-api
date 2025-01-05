package com.nhnacademy.bookapi.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchDTO {
    private Long id;
    private String title; // 도서명
    private String isbn13; // isbn
}