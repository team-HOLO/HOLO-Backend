package com.elice.holo.member.controller;

import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.service.MemberService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(
        @RequestBody MemberSignupRequestDto requestDto) {
        MemberResponseDto memberResponseDto = memberService.signup(requestDto);
        return ResponseEntity.ok(memberResponseDto);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto> login(@RequestBody MemberLoginRequestDto requestDto) {
        MemberResponseDto memberResponseDto = memberService.login(requestDto);
        return ResponseEntity.ok(memberResponseDto);
    }

    // 모든 회원 조회 API
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        List<MemberResponseDto> memberList = memberService.getAllMembers();
        return ResponseEntity.ok(memberList);
    }

    // 특정 회원 조회 API
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(
        @PathVariable(name = "memberId") Long memberId) {
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
