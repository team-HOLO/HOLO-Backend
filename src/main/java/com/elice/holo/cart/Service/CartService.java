package com.elice.holo.cart.Service;

import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.repository.CartRepository;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.repository.MemberRepository;
import com.elice.holo.product.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;


@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart getCart(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    //
    // public void addProductToCart (Long memberId, Product product, Long quantity){


}
