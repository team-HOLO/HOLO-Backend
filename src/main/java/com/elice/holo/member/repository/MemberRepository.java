package com.elice.holo.member.repository;

import com.elice.holo.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberIdAndIsDeletedFalse(Long memberId);

    List<Member> findAllByIsDeletedFalse();

    Optional<Member> findByEmailAndIsDeletedFalse(String email);


}