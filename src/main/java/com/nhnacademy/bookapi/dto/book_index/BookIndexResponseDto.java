package com.nhnacademy.bookapi.dto.book_index;

public class BookIndexResponseDto {

    private String bookIndex;

    public BookIndexResponseDto(String bookIndex) {
        this.bookIndex = bookIndex;
    }

    public void setBookIndex(String bookIndex) {
        this.bookIndex = bookIndex;
    }

    public String getBookIndex() {
        return bookIndex;
    }

}
