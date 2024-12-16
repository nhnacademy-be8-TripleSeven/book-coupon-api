package com.nhnacademy.bookapi.dto.book_index;

import lombok.Getter;

@Getter
public class BookIndexRequestDto {

    private Long bookId;
    private String title;
    private int number;
    private int sequence;

    public BookIndexRequestDto(Long bookId, String title, int number, int sequence) {
        this.bookId = bookId;
        this.title = title;
        this.number = number;
        this.sequence = sequence;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public int getNumber() {
        return number;
    }

    public int getSequence() {
        return sequence;
    }
}
