package com.elice.holo.config.jwt;


import com.elice.holo.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    // 생성자에서 jwtProperties만 주입, secretKey 인코딩 없음
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    // 사용자 정보를 기반으로 JWT 토큰을 생성하는 메서드
    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
    }

    // JWT 토큰을 생성하는 메서드
    private String makeToken(Date expiry, Member member) {
        Date now = new Date();


        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // JWT 타입 설정
            .setIssuer(jwtProperties.getIssuer()) // 발급자 설정
            .setIssuedAt(now) // 발급 시간
            .setExpiration(expiry) // 만료 시간
            .setSubject(member.getEmail()) // 이메일을 주체로 설정
            .claim("memberId", member.getMemberId()) // 사용자 ID 클레임 설정
            .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 인코딩 없이 Secret Key 그대로 사용
            .compact(); // 토큰 생성 및 반환
    }

    // JWT 토큰의 유효성을 검사하는 메서드
    public boolean validToken(String token) {
        try {
            // 토큰 파싱 및 서명 검증
            Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey()) // 비밀 키 그대로 사용
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // 토큰이 유효하지 않으면 false 반환
        }
    }

    // 토큰에서 인증 정보를 추출하는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        // 인증 객체 생성 및 반환
        return new UsernamePasswordAuthenticationToken(
            new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    // 토큰에서 사용자 ID를 추출하는 메서드
    public Long getMemberId(String token) {
        Claims claims = getClaims(token);  // 토큰에서 클레임을 가져옴
        return claims.get("memberId", Long.class);  // 클레임에서 사용자 ID를 추출
    }

    // JWT 토큰에서 클레임을 추출하는 메서드
    private Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey()) // 비밀 키 그대로 사용
            .parseClaimsJws(token)
            .getBody();
    }
}