package com.elice.holo.member.controller;

import com.elice.holo.config.jwt.JwtTokenProvider;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDto requestDto) {
        // 회원가입 후 회원 정보를 엔티티로 받음
        Member member = memberService.signupAndReturnEntity(requestDto);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(member, java.time.Duration.ofHours(2));
        return ResponseEntity.status(201).body("회원가입 성공. JWT Token: " + token);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberLoginRequestDto requestDto) {
        // 로그인 후 회원 정보를 엔티티로 받음
        Member member = memberService.loginAndReturnEntity(requestDto);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(member, java.time.Duration.ofHours(2));
        return ResponseEntity.ok("로그인 성공. JWT Token: " + token);
    }

    // 모든 회원 조회 API
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        List<MemberResponseDto> memberList = memberService.getAllMembers();
        return ResponseEntity.ok(memberList);
    }

    // 특정 회원 조회 API
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable(name = "memberId") Long memberId) {
        MemberResponseDto memberResponseDto = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberResponseDto);
    }

    // 회원 정보 수정 API
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(
        @PathVariable(name = "memberId") Long memberId,
        @RequestBody MemberUpdateRequestDto requestDto) {
        MemberResponseDto updatedMember = memberService.updateMember(memberId, requestDto);
        return ResponseEntity.ok(updatedMember);
    }

    // 회원 삭제 API
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable(name = "memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
