package com.elice.holo.member.service;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberMapper;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper; // MemberMapper 주입

    // 회원 등록
    @Transactional
    public MemberResponseDto signup(MemberSignupRequestDto requestDto) {

        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = memberMapper.toEntity(requestDto);

        memberRepository.save(member);

        return memberMapper.toDto(member);
    }

    // 로그인
    public MemberResponseDto login(MemberLoginRequestDto requestDto) {

        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return memberMapper.toDto(member);
    }

    // 모든 회원 조회
    public List<MemberResponseDto> getAllMembers() {

        List<Member> members = memberRepository.findAllByIsDeletedFalse();

        return memberMapper.toDtoList(members);
    }

    // 특정 회원 조회
    public MemberResponseDto getMemberById(Long memberId) {

        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return memberMapper.toDto(member);
    }

    // 회원 정보 수정
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) {

        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));


        member.updateMemberInfo(requestDto.getName(), requestDto.getTel(),
            requestDto.getAge(), requestDto.getGender());


        memberRepository.save(member);


        return memberMapper.toDto(member);
    }



    // 회원 삭제(비활성화)
    @Transactional
    public void deleteMember(Long memberId) {

        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.deactivateMember();

        memberRepository.save(member);
    }
}
