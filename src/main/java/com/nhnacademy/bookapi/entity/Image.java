package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class Image {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;

    public Image(String url) {
        this.url = url;
    }

    public void update(String url){
        this.url = url;
    }
}
