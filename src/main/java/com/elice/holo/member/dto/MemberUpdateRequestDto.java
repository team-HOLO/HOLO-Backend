package com.elice.holo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//수정 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequestDto {

    private String email;
    private String password;
    private String name;
    private String tel;
    private Boolean gender;
//    private Integer age;
}