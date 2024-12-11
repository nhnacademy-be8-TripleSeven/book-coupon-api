package com.nhnacademy.bookapi.entity;

public enum Role {

    AUTHOR("지은이"),
    ILLUSTRATOR("그림"), // PHOTO를 ILLUSTRATOR로 변경 (의미 명확화)
    EDITOR("엮은이"),
    ORIGINAL_AUTHOR("원작"), // ORIGINAL을 ORIGINAL_AUTHOR로 변경 (명확성)
    TRANSLATOR("옮긴이"),
    SUBTRAHEND("감수"),
    WRITER("글"),
    PHOTO("사진"),
    PASSON("빠숑");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
