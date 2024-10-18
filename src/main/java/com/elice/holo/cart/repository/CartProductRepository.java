package com.elice.holo.cart.repository;

import com.elice.holo.cart.domain.CartProduct;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByCartProductIdAndCart_CartId(Long cartProductId, Long cartId);

    // 추가적인 메서드 예시: 장바구니 내 모든 상품 조회
    List<CartProduct> findByCart_CartId(Long cartId);

}
