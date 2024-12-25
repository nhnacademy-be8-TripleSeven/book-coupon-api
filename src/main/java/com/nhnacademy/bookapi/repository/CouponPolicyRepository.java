package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
    Optional<CouponPolicy> findById(Long id);
    Optional<CouponPolicy> findByName(String name);
    List<CouponPolicy> findByNameContainingIgnoreCase(String name);

}
