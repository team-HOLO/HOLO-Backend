package com.elice.holo.cart.mapper;


import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.dto.CartProductDto;
import com.elice.holo.cart.dto.CartRequestDto;
import com.elice.holo.product.domain.Product;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cartProducts", target = "products")
    @Mapping(target = "totalPrice", ignore = true)
    CartDto toCartDto(Cart cart);

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "color", source = "cartRequestDto.color")
    @Mapping(target = "size", source = "cartRequestDto.size")
    CartProduct toEntity(CartRequestDto cartRequestDto);

    @Mapping(source = "cartProductId", target = "cartProductId")
    @Mapping(source = "product", target = "productId")
    @Mapping(target = "cartId", expression = "java(cartProduct.getCart().getCartId())")
    CartProductDto toCartProductDto(CartProduct cartProduct);


    default Long map(Product product) {
        return product != null ? product.getProductId() : null; // product가 null이 아닐 경우 ID 반환
    }

    List<CartDto> toCartDtoList(List<Cart> carts);
}
