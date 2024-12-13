package com.nhnacademy.bookapi.dto.book_index;

public class BookIndexResponseDto {

    private String title;
    private int number;
    private int sequence;

    public BookIndexResponseDto(String title, int number, int sequence) {
        this.title = title;
        this.number = number;
        this.sequence = sequence;
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
