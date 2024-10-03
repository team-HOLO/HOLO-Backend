package com.elice.holo.member.service;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponseDto signup(MemberSignupRequestDto requestDto) {
        Member member = Member.builder()
            .email(requestDto.getEmail())
            .password(requestDto.getPassword())
            .name(requestDto.getName())
            .tel(requestDto.getTel())
            .gender(requestDto.getGender())
            .age(requestDto.getAge())
            .isAdmin(false) // 회원가입 시 기본값 설정
            .isDeleted(false) // 회원가입 시 기본값 설정
            .build();

        memberRepository.save(member);
        return new MemberResponseDto(member);
    }

    public MemberResponseDto login(MemberLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail());
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return new MemberResponseDto(member);
    }

    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(MemberResponseDto::new).collect(Collectors.toList());
    }

    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        return new MemberResponseDto(member);
    }

    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        member.setPassword(requestDto.getPassword());
        member.setName(requestDto.getName());
        member.setTel(requestDto.getTel());
        member.setGender(requestDto.getGender());
        member.setAge(requestDto.getAge());

        return new MemberResponseDto(member);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        memberRepository.delete(member);
    }
}
