package com.elice.holo.member.repository;

import com.elice.holo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email); // O이메일에 따라 멤버반환, null처리 필요
}