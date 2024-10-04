package com.elice.holo.member.dto;

import com.elice.holo.member.domain.Member;
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

    // 생성자
    public MemberResponseDto(Member member) {
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.tel = member.getTel();
        this.gender = member.getGender();
        this.age = member.getAge();
        this.isAdmin = member.getIsAdmin();
    }
}
