package com.elice.holo.member.domain;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class MemberDetails implements UserDetails {

    private final Member member;

    public MemberDetails(Member member) {
        this.member = member;
    }

    // Member 엔티티의 ID 가져오기
    public Long getMemberId() {
        return member.getMemberId();
    }

    // 권한 정보 제공 (ROLE_USER 또는 ROLE_ADMIN)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getIsAdmin() ?
            Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")) :
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Spring Security에서 사용하는 getUsername()을 memberId로 반환
    @Override
    public String getUsername() {
        return member.getMemberId().toString();  // memberId를 반환
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }



    @Override
    public boolean isEnabled() {
        return !member.getIsDeleted();
    }

    // 추가적으로 필요하다면 Member 엔티티의 정보를 직접 제공할 수 있는 메서드 추가
    public String getName() {
        return member.getName();
    }

    public Boolean getGender() {
        return member.getGender();
    }

//    public Integer getAge() {
//        return member.getAge();
//    }

    public String getTel() {
        return member.getTel();
    }
}
