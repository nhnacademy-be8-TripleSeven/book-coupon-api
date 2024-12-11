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
    @JoinColumn(name = "book_id")
    @Setter
    private Book book;

    @Setter
    private long clickRank;

    @Setter
    private long searchRank;

    @Setter
    private long cartCount;

}
