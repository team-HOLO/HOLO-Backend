package com.elice.holo.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long memberId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "is_admin")
    private Boolean isAdmin; // 관리자 여부

    @Column(name = "is_deleted")
    private Boolean isDeleted; // 회원탈퇴 여부 true : 탈퇴

    @Column(name = "tel")
    private String tel;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private Boolean gender; // (true = 남성, false = 여성 등)


    // 회원 탈퇴 메서드 (isDeleted 값 변경)
    public void deactivateMember() {
        this.isDeleted = true;
    }

    // 회원 정보 수정 메서드
    public void updateMemberInfo(String name, String tel,
        Integer age,
        Boolean gender) {
        this.password = password;
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.age = age;
        this.gender = gender;
    }

    public Member update(String nickname) {
        this.name = nickname;

        return this;
    }


}
