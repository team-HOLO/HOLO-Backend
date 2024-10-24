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
import org.springframework.http.ResponseCookie;
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


    @Value("${spring.redirect.url}")
    private String redirectUrl;


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

    // OAuth2 로그인 성공 시 호출되는 메서드

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Member user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));


        String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2));


        setJwtCookie(response, accessToken, 60 * 60 * 2);


        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}