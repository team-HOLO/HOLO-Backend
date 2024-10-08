package com.elice.holo.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long memberId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin; // 관리자 여부

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted; // 회원탈퇴 여부 true : 탈퇴

    @Column(name = "tel", nullable = false)
    private String tel;

    @Column(name = "age", nullable = true)
    private Integer age;

    @Column(name = "gender", nullable = true)
    private Boolean gender; // (true = 남성, false = 여성 등)

    @Builder
    public Member(Long memberId, String email, String password, String name, Boolean isAdmin,
        Boolean isDeleted,
        String tel, Boolean gender, Integer age) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
        this.isDeleted = isDeleted;
        this.tel = tel;
        this.gender = gender;
        this.age = age;
    }


    // 회원 탈퇴 메서드 (isDeleted 값 변경)
    public void deactivateMember() {
        this.isDeleted = true;
    }

    // 회원 정보 수정 메서드
    public void updateMemberInfo(String name, String email, String tel, Integer age,
        Boolean gender) {
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.age = age;
        this.gender = gender;
    }

    //비밀번호 업데이트 메소드
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
