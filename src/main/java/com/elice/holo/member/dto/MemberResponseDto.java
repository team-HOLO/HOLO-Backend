package com.elice.holo.member.dto;

import com.elice.holo.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {

    private Long memberId;
    private String email;

    private String name;
    private String tel;
    private Boolean gender;
    private Integer age;
    private Boolean isAdmin;
    private String password;

    public MemberResponseDto(Member member) {
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.tel = member.getTel();
        this.gender = member.getGender();
//        this.age = member.getAge();
        this.isAdmin = member.getIsAdmin();
        this.password=member.getPassword();
    }
}