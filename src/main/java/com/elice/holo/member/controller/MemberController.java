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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

    // 쿠키 설정 메소드
    private void setJwtCookie(HttpServletResponse response, String token, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from("jwtToken", token)
            .path("/")
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .maxAge(maxAge)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDto requestDto) {
        Member member = memberService.signupAndReturnEntity(requestDto);
        if (member == null) {
            return ResponseEntity.status(400).body("already existing email!");
        }

        String token = jwtTokenProvider.generateToken(member, Duration.ofHours(2));
        return ResponseEntity.status(201).body("sign up! JWT Token: " + token);
    }

    @Operation(summary = "로그인", description = "회원으로 로그인하고 JWT 토큰을 쿠키에 설정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberLoginRequestDto requestDto, HttpServletResponse response) {
        try {
            Member member = memberService.loginAndReturnEntity(requestDto);
            String token = jwtTokenProvider.generateToken(member, TOKEN_VALIDITY_DURATION);

            setJwtCookie(response, token, TOKEN_VALIDITY_DURATION.toSeconds());

            return ResponseEntity.ok("sign_in complete");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Operation(summary = "로그아웃", description = "사용자를 로그아웃하고 JWT 쿠키를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        setJwtCookie(response, null, 0);
        return ResponseEntity.ok("logout finished");
    }

    @Operation(summary = "모든 회원 조회", description = "모든 회원의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        List<MemberResponseDto> memberList = memberService.getAllMembers();
        return ResponseEntity.ok(memberList);
    }

    @Operation(summary = "특정 회원 조회", description = "주어진 ID에 해당하는 회원의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable(name = "memberId") Long memberId) {
        MemberResponseDto memberResponseDto = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberResponseDto);
    }

    @Operation(summary = "회원 정보 수정", description = "주어진 ID에 해당하는 회원의 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
        @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(
        @PathVariable(name = "memberId") Long memberId,
        @RequestBody MemberUpdateRequestDto requestDto) {
        MemberResponseDto updatedMember = memberService.updateMember(memberId, requestDto);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "회원 삭제", description = "주어진 ID에 해당하는 회원을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "회원 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable(name = "memberId") Long memberId, HttpServletResponse response) {
        memberService.deleteMember(memberId);
        setJwtCookie(response, null, 0);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 확인", description = "현재 사용자가 관리자 권한을 가지고 있는지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "관리자 여부 확인 성공")
    })
    @GetMapping("/check-admin")
    public ResponseEntity<Boolean> checkAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return ResponseEntity.ok(isAdmin);
        }
        return ResponseEntity.ok(false);
    }

    @Operation(summary = "로그인 여부 확인", description = "현재 사용자가 로그인했는지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 여부 확인 성공")
    })
    @GetMapping("/check-login")
    public ResponseEntity<Boolean> checkLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            !(authentication instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @Operation(summary = "현재 로그인한 사용자 정보", description = "현재 로그인한 사용자의 정보를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
    })
    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        MemberResponseDto memberResponseDto = memberService.getMemberById(memberDetails.getMemberId());
        return ResponseEntity.ok(memberResponseDto);
    }
}
