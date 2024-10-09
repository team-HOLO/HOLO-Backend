package com.elice.holo.cart.mapper;


import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.dto.CartProductDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cartProducts", target = "products")
    CartDto toCartDto(Cart cart);

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    CartProduct toEntity(CartProductDto productDto);

    @Mapping(source = "cartProductId", target = "cartProductId")
    @Mapping(source = "product.id", target = "productId")
    CartProductDto toCartProductDto(CartProduct cartProduct);

    List<CartDto> toCartDtoList(List<Cart> carts);
}
