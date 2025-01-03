package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int level;


    public Category(String name, int level) {
        this.name = name;
        this.level = level;
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
