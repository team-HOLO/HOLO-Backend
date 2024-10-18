package com.elice.holo.cart.repository;

import com.elice.holo.cart.domain.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMember_MemberId(Long memberId); //특정 회원의 장바구니 조회


}
