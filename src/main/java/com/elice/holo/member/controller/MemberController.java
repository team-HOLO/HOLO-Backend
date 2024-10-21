package com.elice.holo.member.controller;

import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.token.JwtTokenProvider;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    final Duration TOKEN_VALIDITY_DURATION = Duration.ofHours(2);

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDto requestDto) {
        // 회원가입 후 회원 정보를 엔티티로 받음
        Member member = memberService.signupAndReturnEntity(requestDto);
        if (member == null) {
            return ResponseEntity.status(400).body("already existing email!"); // 이메일 중복 시 에러 반환
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(member, java.time.Duration.ofHours(2));
        return ResponseEntity.status(201).body("sign up! JWT Token: " + token);
    }

    // 로그인 API - 쿠키로 JWT 토큰 전달
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberLoginRequestDto requestDto, HttpServletResponse response) {
        try {
            // 로그인 후 회원 정보를 엔티티로 받음
            Member member = memberService.loginAndReturnEntity(requestDto);

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(member, TOKEN_VALIDITY_DURATION);

            // 쿠키 설정 (HttpOnly, Secure 플래그 적용)
            ResponseCookie cookie = ResponseCookie.from("jwtToken", token)
                    .path("/")
                    .sameSite("None")  // same site를 None으로 설정
                    .httpOnly(true)
                    .secure(true)     // SameSite 설정 시 필수
                    .maxAge(60 * 60 * 2)
                    .build();

            // 쿠키를 응답에 추가: Set-Cookie 헤더 필요
            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok("sign_in complete");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // 로그인 실패 시 400 상태 코드와 메시지 반환
        }
    }
    // 로그아웃 시 쿠키 삭제
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", null); // 쿠키 삭제
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 유효 시간을 0으로 설정하여 삭제
        response.addCookie(cookie);

        return ResponseEntity.ok("logout finished");
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
    @GetMapping("/check-admin")
    public ResponseEntity<Boolean> checkAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {

            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return ResponseEntity.ok(isAdmin);
        }
        return ResponseEntity.ok(false);
    }
    // 로그인 여부 확인 API
    @GetMapping("/check-login")
    public ResponseEntity<Boolean> checkLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(true);  // 로그인된 상태라면 true 반환
        }
        return ResponseEntity.ok(false);  // 로그인되지 않은 상태라면 false 반환
    }
    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> getMyInfo() {
        // SecurityContextHolder에서 현재 인증된 사용자 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 현재 로그인한 사용자의 memberId로 회원 정보 조회
        MemberResponseDto memberResponseDto = memberService.getMemberById(memberDetails.getMemberId());
        return ResponseEntity.ok(memberResponseDto);
    }
}
