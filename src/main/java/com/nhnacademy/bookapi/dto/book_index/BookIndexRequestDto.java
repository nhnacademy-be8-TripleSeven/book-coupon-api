package com.nhnacademy.bookapi.dto.book_index;


public class BookIndexRequestDto {

    private Long bookId;
    private String indexText;
    private int sequence;

    public BookIndexRequestDto() {
    }

    public BookIndexRequestDto(Long bookId, String indexText, int sequence) {
        this.bookId = bookId;
        this.indexText = indexText;
        this.sequence = sequence;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }


    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setIndexText(String indexText) {
        this.indexText = indexText;
    }

    public String getIndexText() {
        return indexText;
    }

    public int getSequence() {
        return sequence;
    }
}
