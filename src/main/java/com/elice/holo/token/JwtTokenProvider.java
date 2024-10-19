package com.elice.holo.token;


import com.elice.holo.member.domain.Member;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Date;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final MemberRepository memberRepository; // MemberRepository 주입


    // 생성자에서 jwtProperties만 주입, secretKey 인코딩 없음
    public JwtTokenProvider(JwtProperties jwtProperties, MemberRepository memberRepository) {
        this.jwtProperties = jwtProperties;
        this.memberRepository = memberRepository;
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
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // JWT 타입
            .setIssuer(jwtProperties.getIssuer()) // 발급자
            .setIssuedAt(now) // 발급 시간
            .setExpiration(expiry) // 만료 시간
            .setSubject(member.getMemberId().toString()) // memberId를 subject로 설정
            .signWith(SignatureAlgorithm.HS256,
                jwtProperties.getSecretKey()) // Secret Key 그대로 사용
            .compact(); // 토큰 생성 및 반환
    }

//    //refresh 토큰 생성
//    public String generateRefreshToken(Member member, Duration expiredAt) {
//        Date now = new Date();
//        return Jwts.builder()
//            .setIssuer(jwtProperties.getIssuer())
//            .setIssuedAt(now)
//            .setExpiration(new Date(now.getTime() + expiredAt.toMillis()))
//            .setSubject(member.getEmail())
//            .claim("memberId", member.getMemberId())
//            .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
//            .compact();
//    }

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

    // 토큰에서 인증 정보를 추출
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Long memberId = Long.parseLong(claims.getSubject()); // subject를 memberId로 사용

        // memberId로 Member 엔티티 조회
        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자"));

        MemberDetails memberDetails = new MemberDetails(member); // MemberDetails 생성

        return new UsernamePasswordAuthenticationToken(
            memberDetails, token, memberDetails.getAuthorities());
    }

    // 토큰에서 사용자 ID를 추출
    public Long getMemberId(String token) {
        Claims claims = getClaims(token);  // 토큰에서 클레임을 가져옴
        return claims.get("memberId", Long.class);  // 클레임에서 사용자 ID를 추출
    }

    // JWT 토큰에서 클레임을 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey()) // 비밀 키 그대로 사용
            .parseClaimsJws(token)
            .getBody();
    }
}