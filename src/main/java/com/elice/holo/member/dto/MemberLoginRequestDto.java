package com.elice.holo.member.dto;

import lombok.Getter;
import lombok.Setter;


//로그인요청시
@Getter
@Setter
public class MemberLoginRequestDto {

    private String email;
    private String password;
}
