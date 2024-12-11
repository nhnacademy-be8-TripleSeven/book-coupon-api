package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}