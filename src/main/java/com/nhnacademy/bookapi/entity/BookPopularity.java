package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class BookPopularity {

    @Id
    private long bookId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
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
