package com.elice.holo.cart.mapper;

import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.dto.CartProductDto;
import com.elice.holo.cart.dto.CartRequestDto;
import com.elice.holo.product.domain.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-18T03:09:58+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartDto toCartDto(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartDto.CartDtoBuilder cartDto = CartDto.builder();

        cartDto.products( cartProductListToCartProductDtoList( cart.getCartProducts() ) );
        cartDto.cartId( cart.getCartId() );

        return cartDto.build();
    }

    @Override
    public CartProduct toEntity(CartRequestDto cartRequestDto) {
        if ( cartRequestDto == null ) {
            return null;
        }

        String color = null;
        String size = null;
        Long quantity = null;

        color = cartRequestDto.getColor();
        size = cartRequestDto.getSize();
        quantity = (long) cartRequestDto.getQuantity();

        Cart cart = null;
        Product product = null;
        Long cartProductId = null;

        CartProduct cartProduct = new CartProduct( cartProductId, cart, product, quantity, color, size );

        return cartProduct;
    }

    @Override
    public CartProductDto toCartProductDto(CartProduct cartProduct) {
        if ( cartProduct == null ) {
            return null;
        }

        CartProductDto.CartProductDtoBuilder cartProductDto = CartProductDto.builder();

        cartProductDto.cartProductId( cartProduct.getCartProductId() );
        cartProductDto.productId( map( cartProduct.getProduct() ) );
        cartProductDto.quantity( cartProduct.getQuantity() );

        cartProductDto.cartId( cartProduct.getCart().getCartId() );

        return cartProductDto.build();
    }

    @Override
    public List<CartDto> toCartDtoList(List<Cart> carts) {
        if ( carts == null ) {
            return null;
        }

        List<CartDto> list = new ArrayList<CartDto>( carts.size() );
        for ( Cart cart : carts ) {
            list.add( toCartDto( cart ) );
        }

        return list;
    }

    protected List<CartProductDto> cartProductListToCartProductDtoList(List<CartProduct> list) {
        if ( list == null ) {
            return null;
        }

        List<CartProductDto> list1 = new ArrayList<CartProductDto>( list.size() );
        for ( CartProduct cartProduct : list ) {
            list1.add( toCartProductDto( cartProduct ) );
        }

        return list1;
    }
}
