package com.elice.holo.order.repository;

import com.elice.holo.order.domain.Order;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


//주문 정보를 데이터베이스에서 조회, 저장, 삭제하는 인터페이스
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 회원 ID로 주문 조회
    List<Order> findByMember_MemberId(Long memberId);

    // 회원 ID로 주문 조회 (소프트 딜리트된 주문 제외)
    List<Order> findByMember_MemberIdAndIsDeletedFalse(Long memberId);

    // 모든 주문 조회 (소프트 딜리트된 주문 제외)
    List<Order> findAllByIsDeletedFalse();

    // 특정 주문 조회 (소프트 딜리트된 주문 제외)
    Optional<Order> findByIdAndIsDeletedFalse(Long orderId);

}

