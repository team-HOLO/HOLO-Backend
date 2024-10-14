package com.elice.holo.token;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.service.MemberService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    // 리프레시 토큰을 통해 새로운 액세스 토큰을 생성하는 메서드
    public String createNewAccessToken(String refreshToken) {
        // 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }


        Long memberId = jwtTokenProvider.getMemberId(refreshToken);
        Member member = memberService.getMemberEntityById(memberId);// 서비스에서 사용자 조회


        return jwtTokenProvider.generateToken(member, Duration.ofHours(2));
    }
}