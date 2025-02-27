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
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(jwtProperties.getIssuer())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setSubject(member.getMemberId().toString())
            .signWith(SignatureAlgorithm.HS256,
                jwtProperties.getSecretKey())
            .compact();
    }



    // JWT 토큰의 유효성을 검사하는 메서드
    public boolean validToken(String token) {
        try {

            Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 인증 정보를 추출
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Long memberId = Long.parseLong(claims.getSubject());


        Member member = memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자"));

        MemberDetails memberDetails = new MemberDetails(member);

        return new UsernamePasswordAuthenticationToken(
            memberDetails, token, memberDetails.getAuthorities());
    }

    // 토큰에서 사용자 ID를 추출
    public Long getMemberId(String token) {
        Claims claims = getClaims(token);
        return claims.get("memberId", Long.class);
    }

    // JWT 토큰에서 클레임을 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey())
            .parseClaimsJws(token)
            .getBody();
    }
}