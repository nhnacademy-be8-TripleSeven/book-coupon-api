package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;

@Entity
public class BookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Tag tag;

}
