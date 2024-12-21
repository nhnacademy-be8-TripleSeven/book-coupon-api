package com.nhnacademy.bookapi.dto.book_index;

import lombok.Getter;

@Getter
public class BookIndexRequestDto {

    private Long bookId;
    private String bookText;

    public BookIndexRequestDto(Long bookId, String title, int number, int sequence) {
        this.bookId = bookId;
        this.bookText = bookText;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setBookText(String title) {
        this.bookText = bookText;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookText() {
        return bookText;
    }

}
