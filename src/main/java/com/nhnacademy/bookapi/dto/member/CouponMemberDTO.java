package com.nhnacademy.bookapi.dto.member;

import com.nhnacademy.bookapi.entity.MemberGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CouponMemberDTO {
    private Long id; // 회원 ID
    private MemberGrade memberGrade; // 회원 등급
}
