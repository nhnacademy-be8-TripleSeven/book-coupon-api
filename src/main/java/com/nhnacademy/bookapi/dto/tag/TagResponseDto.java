package com.nhnacademy.bookapi.dto.tag;

import lombok.Getter;

@Getter
public class TagResponseDto {

    private Long tagId;
    private String tagName;

    public TagResponseDto(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public void setId(Long tagId) {
        this.tagId = tagId;
    }

    public void setName(String tagName) {
        this.tagName = tagName;
    }

    public Long getId() {
        return tagId;
    }

    public String getName() {
        return tagName;
    }
}
