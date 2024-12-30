package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
@RequiredArgsConstructor
public class BookSearchResponseDTO {

    private String id;
    private String title;
    private String isbn13;
    private String description;
    private LocalDateTime publishDate;
    private int regularPrice;
    private int salePrice;
    private int stock;
    private int page;
    private int bestSellerRank;
    private int clickCount;
    private int searchCount;
    private int cartCount;
    private String coverUrl;
    private String publisherName;
    private String bookcreator;
    private List<String> categories;

    public BookSearchResponseDTO(BookDocument bookDocument) {
    }
}
