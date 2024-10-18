package com.elice.holo.member.service;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberMapper;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.exception.AccessDeniedException;
import com.elice.holo.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    // 회원 등록
    @Transactional
    public MemberResponseDto signup(MemberSignupRequestDto requestDto) {
        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            return null;
        }

        Member member = memberMapper.toEntity(requestDto);
        memberRepository.save(member);

        return memberMapper.toDto(member);
    }

    // 회원 등록 후 엔티티 반환
    @Transactional
    public Member signupAndReturnEntity(MemberSignupRequestDto requestDto) {
        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = memberMapper.toEntity(requestDto);
        memberRepository.save(member);

        return member;
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

    // 로그인 후 엔티티 반환
    public Member loginAndReturnEntity(MemberLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!member.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    // 모든 회원 조회
    public List<MemberResponseDto> getAllMembers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 관리자가 아니면 권한 에러
        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        List<Member> members = memberRepository.findAllByIsDeletedFalse();
        return memberMapper.toDtoList(members);
    }

    // 특정 회원 조회
    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 로그인한 사용자가 해당 회원의 정보를 조회할 권한이 있는지 확인
        if (!memberDetails.getMemberId().equals(memberId) &&
            !memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("조회 권한이 없습니다.");
        }

        return memberMapper.toDto(member);
    }

    public Member getMemberEntityById(Long memberId) {
        return memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // 회원 정보 수정 (권한 검증 추가)
    @Transactional
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 로그인한 사용자가 해당 회원의 정보를 수정할 권한이 있는지 확인
        if (!memberDetails.getMemberId().equals(memberId) &&
            !memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        member.updateMemberInfo(requestDto.getName(), requestDto.getTel(), requestDto.getAge(), requestDto.getGender());
        memberRepository.save(member);

        return memberMapper.toDto(member);
    }

    // 회원 삭제(비활성화)
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 로그인한 사용자가 해당 회원을 삭제할 권한이 있는지 확인
        if (!memberDetails.getMemberId().equals(memberId) &&
            !memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        member.deactivateMember();
        memberRepository.save(member);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmailAndIsDeletedFalse(email)
            .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
