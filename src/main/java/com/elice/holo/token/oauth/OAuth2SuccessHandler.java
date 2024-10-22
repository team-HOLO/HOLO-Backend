package com.elice.holo.token.oauth;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.service.MemberService;
import com.elice.holo.token.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

// OAuth2SuccessHandler 클래스는 OAuth2 로그인 성공 후 동작을 정의
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // JWT 토큰 제공자
    private final JwtTokenProvider tokenProvider;
    private final MemberService userService;
    @Value("${spring.redirect.url}") // YML에서 redirect URL을 가져옴
    private String redirectUrl;
    // OAuth2 로그인 성공 시 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 인증된 사용자의 정보를 가져옴
        Member user = userService.findByEmail((String) oAuth2User.getAttributes().get("email")); // 사용자 정보 조회

        // JWT 토큰 생성
        String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2)); // JWT 토큰을 2시간 동안 유효하도록 생성

        // JWT 토큰을 쿠키에 설정 (HttpOnly, Secure 플래그 적용)
        Cookie jwtCookie = new Cookie("jwtToken", accessToken);
        jwtCookie.setHttpOnly(true); // XSS 공격 방지
        jwtCookie.setSecure(true); // HTTPS에서만 전송
        jwtCookie.setPath("/"); // 애플리케이션 전체에서 쿠키가 유효하도록 설정
        jwtCookie.setMaxAge(60 * 60 * 2); // 2시간 동안 쿠키 유지

        // 쿠키를 응답에 추가
        response.addCookie(jwtCookie);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}