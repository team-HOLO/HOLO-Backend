package com.elice.holo.member.dto;

import com.elice.holo.member.domain.Member;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MemberMapper {

    // 회원가입용 DTO -> 엔티티 변환

    @Mapping(target = "isDeleted", constant = "false")
    Member toEntity(MemberSignupRequestDto requestDto);

    // 엔티티 -> DTO 변환
    @Mapping(source = "memberId", target = "memberId")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "tel", target = "tel")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "age", target = "age")
    @Mapping(source = "isAdmin", target = "isAdmin")
    MemberResponseDto toDto(Member member);



    // 엔티티 리스트 -> DTO 리스트 변환
    List<MemberResponseDto> toDtoList(List<Member> members);
}