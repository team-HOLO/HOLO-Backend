package com.elice.holo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//로그인요청시
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequestDto {

    private String email;
    private String password;
}
