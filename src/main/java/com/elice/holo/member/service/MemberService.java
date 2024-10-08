package com.elice.holo.member.service;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberMapper;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;


    //회원등록
    @Transactional
    public MemberResponseDto signup(MemberSignupRequestDto requestDto) {
        // 이메일 중복 확인
        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = MemberMapper.toEntity(requestDto);

        memberRepository.save(member);

        return MemberMapper.toDto(member);
    }

    //로그인
    public MemberResponseDto login(MemberLoginRequestDto requestDto) {

        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return MemberMapper.toDto(member);
    }

    //모든 회원조회
    public List<MemberResponseDto> getAllMembers() {

        List<Member> members = memberRepository.findAllByIsDeletedFalse();

        List<MemberResponseDto> memberResponseDtos = new ArrayList<>();
        for (Member member : members) {
            memberResponseDtos.add(MemberMapper.toDto(member));
        }

        return memberResponseDtos;
    }

    //특정회원 조회
    public MemberResponseDto getMemberById(Long memberId) {

        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return MemberMapper.toDto(member);
    }

    //회원수정함수
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) {

        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.updateMemberInfo(requestDto.getName(), requestDto.getEmail(), requestDto.getTel(),
            requestDto.getAge(), requestDto.getGender());

        memberRepository.save(member);

        return MemberMapper.toDto(member);
    }

    //회원삭제 함수
    @Transactional
    public void deleteMember(Long memberId) {

        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.deactivateMember();
        memberRepository.save(member);
    }
}