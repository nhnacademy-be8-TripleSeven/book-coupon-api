package com.nhnacademy.bookapi.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookPopularityDTO {

    private String id;
    private long popularity;
}
