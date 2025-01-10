package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookCreatorRepository extends JpaRepository<BookCreator, Long> {

    @Query("select b from BookCreator b where b.name =:name and b.role =:role")
    BookCreator existByNameAndRole(String name, Role role);

    @Query("select bc from BookCreator bc left JOIN BookCreatorMap bcm on bcm.creator.id = bc.id left JOIN Book b on b.id = bcm.book.id where b.id =:id")
    List<BookCreator> findCreatorByBookId(@Param("id") Long id);


    Optional<BookCreator> findCreatorByName(String name);



}
