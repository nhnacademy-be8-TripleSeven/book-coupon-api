package com.nhnacademy.bookapi.client;

import com.nhnacademy.bookapi.dto.member.CouponMemberDTO;
import com.nhnacademy.bookapi.entity.MemberGrade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "member-api")
public interface MemberFeignClient {

    @GetMapping("/api/members")
    List<CouponMemberDTO> getAllMembers();

    @GetMapping("/api/members/grade")
    List<CouponMemberDTO> getMembersByGrade(@RequestParam("grade") MemberGrade grade);
}
