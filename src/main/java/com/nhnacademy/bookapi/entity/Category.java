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

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;


    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public void create(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public void update(String newName, Category newParent) {
        this.name = newName;
        this.parent = newParent;
    }

    public void setTestId(Long id) {
        this.id = id;
    }
    
    public Category(String name) {
        this.name = name;
    }

}
