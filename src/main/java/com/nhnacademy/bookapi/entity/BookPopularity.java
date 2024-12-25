package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class BookPopularity {

    @Id
    private long bookId;

    @MapsId
    @OneToOne
    private Book book;

    private long clickRank;

    private long searchRank;

    private long cartCount;

    public BookPopularity(Book book, long clickRank, long searchRank, long cartCount) {
        this.book = book;
        this.clickRank = clickRank;
        this.searchRank = searchRank;
        this.cartCount = cartCount;
    }

    public void create(Book book) {
        this.book = book;
        this.clickRank = 0;
        this.searchRank = 0;
        this.cartCount = 0;
    }

    public void update(long clickRank, long searchRank, long cartCount) {
        this.clickRank = clickRank;
        this.searchRank = searchRank;
        this.cartCount = cartCount;
    }

}
