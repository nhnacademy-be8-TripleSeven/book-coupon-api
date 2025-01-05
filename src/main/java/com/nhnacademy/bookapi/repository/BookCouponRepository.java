package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCoupon;
import com.nhnacademy.bookapi.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
    Optional<BookCoupon> findByCoupon(Coupon coupon);



    @Modifying
    @Query("delete from BookCoupon bc where bc.book.id =:bookId")
    void deleteByBookId(Long bookId);

//    @Query("SELECT bc FROM BookCoupon bc JOIN FETCH bc.book WHERE bc.coupon = :coupon")
//    Optional<BookCoupon> findByCouponWithBook(Coupon coupon);

}
