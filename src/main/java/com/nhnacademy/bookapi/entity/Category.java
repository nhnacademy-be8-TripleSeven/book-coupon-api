package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int level;

    @ManyToOne
    private Category parent;

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

    public void create(String name, int level) {
        this.name = name;
        this.level = level;
    }


    public void update(String newName, int level) {
        this.name = newName;
        this.level = level;
    }


    public void setTestId(Long id) {
        this.id = id;
    }
    
    public Category(String name) {
        this.name = name;
    }

}
