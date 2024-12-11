package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookCreatorRepository extends JpaRepository<BookCreator, Long> {

    @Query("select b from BookCreator b where b.name =:name and b.role =:role")
    BookCreator existByNameAndRole(String name, Role role);
}
