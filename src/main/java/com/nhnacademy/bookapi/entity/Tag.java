package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
}