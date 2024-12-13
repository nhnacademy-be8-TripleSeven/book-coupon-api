package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCoupon;
import com.nhnacademy.bookapi.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
    Optional<BookCoupon> findByCoupon(Coupon coupon);
}
