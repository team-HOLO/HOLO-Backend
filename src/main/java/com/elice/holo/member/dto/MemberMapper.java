package com.elice.holo.member.dto;

import com.elice.holo.member.domain.Member;

public class MemberMapper {

    // DTO -> Entity (회원가입용)
    public static Member toEntity(MemberSignupRequestDto requestDto) {
        return Member.builder()
            .email(requestDto.getEmail())
            .password(requestDto.getPassword())
            .name(requestDto.getName())
            .tel(requestDto.getTel())
            .gender(requestDto.getGender())
            .age(requestDto.getAge())
            .isAdmin(false)
            .isDeleted(false)
            .build();
    }

    // Entity -> DTO
    public static MemberResponseDto toDto(Member member) {
        return new MemberResponseDto(member);
    }
}