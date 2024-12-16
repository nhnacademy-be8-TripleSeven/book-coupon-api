package com.nhnacademy.bookapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Setter
    @Column(nullable = false)
    private LocalDate publishDate;
    @Setter
    @Column(nullable = false)
    private int regularPrice;
    @Setter
    @Column(nullable = false)
    private int salePrice;
    @Setter
    @Column(nullable = false, length = 50)
    private String isbn13;
    @Setter
    @Column(nullable = false)
    private int stock;
    @Setter
    @Column(nullable = false)
    private int page;


    @Setter
    @ManyToOne
    private Publisher publisher;

  
    public void setTestId(Long id) {
        this.id = id;
    }
}

