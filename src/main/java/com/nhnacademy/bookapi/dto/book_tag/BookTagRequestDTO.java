package com.nhnacademy.bookapi.dto.book_tag;

import jakarta.validation.constraints.NotNull;

public class BookTagRequestDTO {

    @NotNull
    private Long bookId;

    @NotNull
    private Long tagId;

    public BookTagRequestDTO() {
    }

    public BookTagRequestDTO(@NotNull Long bookId, @NotNull Long tagId) {
        this.bookId = bookId;
        this.tagId = tagId;
    }

    public void setBookId(@NotNull Long bookId) {
        this.bookId = bookId;
    }

    public void setTagId(@NotNull Long tagId) {
        this.tagId = tagId;
    }

    public @NotNull Long getBookId() {
        return bookId;
    }

    public @NotNull Long getTagId() {
        return tagId;
    }
}
