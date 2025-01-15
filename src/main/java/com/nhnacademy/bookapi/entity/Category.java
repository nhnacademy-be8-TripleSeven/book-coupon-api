package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int level;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;


    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<BookCategory> bookCategories = new ArrayList<>();

    public Category(String name, int level, Category parent) {
        this.name = name;
        this.level = level;
        this.parent = parent;
    }

    public Category(String name, int level) {
        this.name = name;
        this.level = level;
        this.parent = null;
    }

    public void create(String name, int level, Category parent) {
        this.name = name;
        this.level = level;
        this.parent = parent;
    }


    public void update(String newName, int level, Category parent) {
        this.name = newName;
        this.level = level;
        this.parent = parent;
    }
    
    public Category(String name) {
        this.name = name;
    }

}
