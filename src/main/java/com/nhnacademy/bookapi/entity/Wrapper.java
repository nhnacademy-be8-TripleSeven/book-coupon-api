package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@Getter
public class Wrapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private Book book;

    @Column(nullable = false)
    private boolean wrappable;

    public Wrapper(Book book, boolean wrappable) {
        this.book = book;
        this.wrappable = wrappable;
    }

    public void updateWrappable(boolean newWrappable) {
        this.wrappable = newWrappable;
    }
}
