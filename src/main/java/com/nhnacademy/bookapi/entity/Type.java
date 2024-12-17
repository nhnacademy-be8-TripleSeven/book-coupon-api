package com.nhnacademy.bookapi.entity;

public enum Type {

    ITEMNEWALL("신간"),
    BESTSELLER("베스트셀러"),
    ITEMNEWSPECIAL("뉴스페셜"),
    ITEMEDITORCHOICE("에디터초이스"),
    BLOGBEST("블로그베스트"),
    EBOOK("e북"),
    FOREIGN("외국도서"),
    BOOK("일반도서");


    private final String description;

    Type(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
