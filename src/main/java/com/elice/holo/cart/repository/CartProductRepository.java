package com.elice.holo.cart.repository;

import com.elice.holo.cart.domain.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

}
