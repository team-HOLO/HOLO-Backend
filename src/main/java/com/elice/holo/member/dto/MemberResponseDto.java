package com.elice.holo.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String name;
    private String tel;
    private Boolean gender;
    private Integer age;
    private Boolean isAdmin;
}