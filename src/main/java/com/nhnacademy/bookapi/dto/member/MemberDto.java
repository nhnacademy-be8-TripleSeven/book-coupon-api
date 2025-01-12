package com.nhnacademy.bookapi.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Date;


@AllArgsConstructor
@Getter
public class MemberDto {

    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private Date birth;
    private String gender;
    private String memberGrade;
}