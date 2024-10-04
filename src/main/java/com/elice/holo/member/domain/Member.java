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
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false) // 스네이크 케이스로 수정
    private Long memberId;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin; // 관리자 여부

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted; // 회원탈퇴 여부 true : 탈퇴

    @Column(name = "tel", nullable = false, length = 255)
    private String tel;

    @Column(name = "age", nullable = true)
    private Integer age;

    @Column(name = "gender", nullable = true)
    private Boolean gender; // (true = 남성, false = 여성 등)

    @Builder
    public Member(String email, String password, String name, Boolean isAdmin, Boolean isDeleted,
        String tel, Boolean gender, Integer age) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
        this.isDeleted = isDeleted;
        this.tel = tel;
        this.gender = gender;
        this.age = age;
    }
}
