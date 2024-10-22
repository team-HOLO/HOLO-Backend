package com.elice.holo.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.elice.holo.member.service.MemberService;
import com.elice.holo.token.JwtAuthenticationFilter;
import com.elice.holo.token.JwtTokenProvider;
import com.elice.holo.token.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.elice.holo.token.oauth.OAuth2SuccessHandler;
import com.elice.holo.token.oauth.OAuth2UserCustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwttokenProvider;
    private final MemberService memberService;
    private final OAuth2UserCustomService oAuth2UserCustomService;

    @Bean
    @Profile("dev")
    public WebSecurityCustomizer devConfigure() {
        return (web) -> web.ignoring()
            .requestMatchers(toH2Console()) // H2 콘솔 무시
            .requestMatchers("/img/**", "/css/**", "/js/**"); // 정적 리소스 무시
    }

    @Bean
    @Profile("prod")
    public WebSecurityCustomizer ProdConfigure() {
        return (web) -> web.ignoring()
            .requestMatchers("/img/**", "/css/**", "/js/**"); // 정적 리소스 무시
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors().and()
            .csrf(csrf -> csrf.disable()) // CSRF 설정 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS)) // 세션을 Stateless로 설정
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/members/signup", "/api/members/login",
                    "/api/members/check-admin", "/api/members/check-login")
                .permitAll() // 회원 가입, 로그인 API는 인증 없이 접근 가능
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .permitAll() // Swagger UI 경로 허용
                .requestMatchers("/api/members/**").authenticated() // 그 외 /api/members 경로는 인증 필요
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll() // 나머지 요청은 모두 허용
            )
            .addFilterBefore(tokenAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
            .exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new AntPathRequestMatcher("/api/members/**"))
            );

        // OAuth2 로그인 설정
        http.oauth2Login()

            .authorizationEndpoint()
            .authorizationRequestRepository(
                oAuth2AuthorizationRequestBasedOnCookieRepository()) // 쿠키 기반 OAuth2 요청 저장소 사용
            .and()
            .successHandler(oAuth2SuccessHandler()) // 성공 핸들러 설정
            .userInfoEndpoint()
            .userService(oAuth2UserCustomService); // 사용자 정보 서비스 설정

        // 예외 처리 - 인증되지 않은 사용자가 /api/** 경로에 접근할 경우 401 Unauthorized 응답
        http.exceptionHandling()
            .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new AntPathRequestMatcher("/api/**"));

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwttokenProvider,
            memberService); // RefreshTokenRepository 제거
    }

    // 쿠키 기반 OAuth2 요청 저장소 설정
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public JwtAuthenticationFilter tokenAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwttokenProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
