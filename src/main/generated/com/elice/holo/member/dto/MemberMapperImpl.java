package com.elice.holo.member.dto;

import com.elice.holo.member.domain.Member;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-19T04:57:11+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(MemberSignupRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.email( requestDto.getEmail() );
        member.password( requestDto.getPassword() );
        member.name( requestDto.getName() );
        member.isAdmin( requestDto.getIsAdmin() );
        member.tel( requestDto.getTel() );
        member.age( requestDto.getAge() );
        member.gender( requestDto.getGender() );

        member.isDeleted( false );

        return member.build();
    }

    @Override
    public MemberResponseDto toDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponseDto memberResponseDto = new MemberResponseDto();

        memberResponseDto.setMemberId( member.getMemberId() );
        memberResponseDto.setEmail( member.getEmail() );
        memberResponseDto.setName( member.getName() );
        memberResponseDto.setTel( member.getTel() );
        memberResponseDto.setGender( member.getGender() );
        memberResponseDto.setAge( member.getAge() );
        memberResponseDto.setIsAdmin( member.getIsAdmin() );

        return memberResponseDto;
    }

    @Override
    public List<MemberResponseDto> toDtoList(List<Member> members) {
        if ( members == null ) {
            return null;
        }

        List<MemberResponseDto> list = new ArrayList<MemberResponseDto>( members.size() );
        for ( Member member : members ) {
            list.add( toDto( member ) );
        }

        return list;
    }
}
