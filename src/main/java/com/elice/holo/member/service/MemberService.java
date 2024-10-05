package com.elice.holo.member.service;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    //의존성 주입
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //새로운 회원 등록 메소드
    @Transactional //db 상태 변경
    public MemberResponseDto signup(MemberSignupRequestDto requestDto) {
        // 이미 존재하는 이메일인지 확인
        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
            .email(requestDto.getEmail())
            .password(requestDto.getPassword())
            .name(requestDto.getName())
            .tel(requestDto.getTel())
            .gender(requestDto.getGender())
            .age(requestDto.getAge())
            .isAdmin(false) // 회원가입 시 기본값 설정, 일반회원 의미
            .isDeleted(false) // 회원가입 시 기본값 설정
            .build();

        memberRepository.save(member);
        return new MemberResponseDto(member);
    }

    //회원 로그인 이메일로 회원 조회하고 비밀번호 일치하는 지 확인
    public MemberResponseDto login(MemberLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
            .orElse(null);
        if (member == null) { //orElse(null) 로직, 멤버가 null 일 경우
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return new MemberResponseDto(member);
    }

    //모든 회원 정보 목록
    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAllByIsDeletedFalse();
        List<MemberResponseDto> memberResponseDtos = new ArrayList<>();

        for (Member member : members) {
            memberResponseDtos.add(new MemberResponseDto(member));
        }

        return memberResponseDtos;
    }

    //특정 회원의 ID로 회원 조회
    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        return new MemberResponseDto(member);
    }

    @Transactional //db 상태 변경
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        member.setEmail(requestDto.getEmail());
        member.setPassword(requestDto.getPassword());
        member.setName(requestDto.getName());
        member.setTel(requestDto.getTel());
        member.setGender(requestDto.getGender());
        member.setAge(requestDto.getAge());

        return new MemberResponseDto(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 실제 삭제 대신 isDeleted를 true로 설정
        member.setIsDeleted(true);
    }
}
