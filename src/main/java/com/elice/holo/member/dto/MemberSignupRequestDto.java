package com.elice.holo.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequestDto {
    private String email;
    private String password;
    private String name;
    private String tel;
    private Boolean gender;
    private Integer age;
}