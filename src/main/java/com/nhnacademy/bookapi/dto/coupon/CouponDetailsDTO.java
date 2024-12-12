package com.nhnacademy.bookapi.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CouponDetailsDTO {
    private Long id;
    private String policyName;
    private String name;
    private Long memberId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private LocalDateTime usedAt;
    private String bookTitle;
    private String categoryName;
}
