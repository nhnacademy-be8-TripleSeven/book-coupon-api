package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
public class BookPopularity {

    @Id
    private long bookId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private long clickRank;

    private long searchRank;

    private long cartCount;

}
