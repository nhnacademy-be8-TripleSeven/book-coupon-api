package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class Wrappable {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private Book book;

    @Setter
    @Column(nullable = false)
    private boolean wrappable;

    public Wrappable(Book book, boolean wrappable) {
        this.book = book;
        this.wrappable = wrappable;
    }
}
