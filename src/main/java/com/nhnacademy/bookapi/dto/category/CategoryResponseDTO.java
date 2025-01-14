package com.nhnacademy.bookapi.dto.category;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private int level;
    private List<CategoryResponseDTO> children;

    // 생성자
    public CategoryResponseDTO(Long id, String name, int level, List<CategoryResponseDTO> children) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.children = (children != null) ? children : new ArrayList<>();
    }

    public CategoryResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter, Setter 등

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public List<CategoryResponseDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryResponseDTO> children) {
        this.children = children;
    }

    // toString() 메소드 (디버깅 용도)
    @Override
    public String toString() {
        return "CategoryDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", level=" + level +
            ", children=" + children +
            '}';
    }
}