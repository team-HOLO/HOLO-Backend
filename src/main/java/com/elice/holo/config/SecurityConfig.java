package com.elice.holo.config;


import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.elice.holo.token.JwtAuthenticationFilter;
import com.elice.holo.token.JwtTokenProvider;
import com.elice.holo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
            .requestMatchers(toH2Console()) // H2 콘솔 무시
            .requestMatchers("/img/**", "/css/**", "/js/**"); // 정적 리소스 무시
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable) // CSRF 설정 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS)) // 세션을 Stateless로 설정
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/members/signup", "/api/members/login")
                .permitAll() // 회원 가입, 로그인 API는 인증 없이 접근 가능
                .requestMatchers("/api/members/**").authenticated() // 그 외 /api/members 경로는 인증 필요
                .anyRequest().permitAll() // 나머지 요청은 모두 허용
            )
            .addFilterBefore(tokenAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
            .exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new AntPathRequestMatcher("/api/members/**"))
            );

        return http.build();
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