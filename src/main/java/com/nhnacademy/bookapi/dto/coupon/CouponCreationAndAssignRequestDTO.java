package com.nhnacademy.bookapi.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CouponCreationAndAssignRequestDTO {
    private String name;                 // 쿠폰 이름
    private Long couponPolicyId;         // 쿠폰 정책 ID
    private Long categoryId;             // 카테고리 ID (카테고리 쿠폰일 경우)
    private Long bookId;                 // 도서 ID (도서 쿠폰일 경우)
    private String grade;           // Member 등급
    private List<Long> memberIds;        // 발급 대상 회원 ID 리스트
    private String recipientType;        // 발급 대상 유형 (전체, 등급별, 개인별)

    public CouponCreationAndAssignRequestDTO(String name, Long couponPolicyId, List<Long> memberIds, String recipientType) {
        this.name = name;
        this.couponPolicyId = couponPolicyId;
        this.memberIds = memberIds;
        this.recipientType = recipientType;
    }

}
