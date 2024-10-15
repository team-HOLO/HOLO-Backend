package com.elice.holo.token.oauth;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.repository.MemberRepository;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final MemberRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest); // 요청을 바탕으로 유저 정보를 담은 객체 반환
        saveOrUpdate(user);

        return user;
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
    private Member saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        Member user = userRepository.findByEmailAndIsDeletedFalse(email)
                .map(entity -> entity.update(name))
                .orElse(Member.builder()
                        .email(email)
                        .name(name)
                    .isDeleted(Boolean.FALSE)
                        .build());

        return userRepository.save(user);
    }
}

