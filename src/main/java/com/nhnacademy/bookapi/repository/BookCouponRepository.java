package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.BookCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
}
