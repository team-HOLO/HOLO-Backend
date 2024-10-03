package com.elice.holo.order.repository;

import com.elice.holo.order.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

// 주문 상품 정보를 데이터베이스에서 조회, 저장, 삭제하는 인터페이스
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

}
