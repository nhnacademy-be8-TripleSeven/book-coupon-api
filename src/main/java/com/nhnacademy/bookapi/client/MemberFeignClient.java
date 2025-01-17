package com.nhnacademy.bookapi.client;

import com.nhnacademy.bookapi.dto.member.MemberDto;
import com.nhnacademy.bookapi.entity.MemberGrade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "member-api")
public interface MemberFeignClient {

    @GetMapping("/admin/members")
    Page<MemberDto> getMembers(@RequestParam(required = false) String name,
                               @RequestParam(required = false) MemberGrade memberGrade,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "ASC") String sortOrder);

}
