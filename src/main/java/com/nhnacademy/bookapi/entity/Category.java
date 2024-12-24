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

    @Setter
    private String name;
  
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    public void setTestId(Long id) {
        this.id = id;
    }
}
