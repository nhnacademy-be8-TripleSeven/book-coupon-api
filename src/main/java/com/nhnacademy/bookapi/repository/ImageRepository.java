package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
