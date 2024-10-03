package com.elice.holo.member.dto;

import lombok.Getter;
import lombok.Setter;


//수정 요청
@Getter
@Setter
public class MemberUpdateRequestDto {

    private String password;
    private String name;
    private String tel;
    private Boolean gender;
    private Integer age;
}