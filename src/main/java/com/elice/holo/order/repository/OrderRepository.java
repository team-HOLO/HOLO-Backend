package com.elice.holo.order.repository;

import com.elice.holo.order.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


//주문 정보를 데이터베이스에서 조회, 저장, 삭제하는 인터페이스
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMember_MemberId(Long memberId); // 회원 ID로 주문 조회

    @Query("SELECT o FROM Order o")
    List<Order> findAllOrders(); //관리자가 모든 회원들의 주문 주회
}
