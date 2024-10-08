package com.elice.holo.cart.mapper;


import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.dto.CartProductDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    CartDto toCartDto(Cart cart);

    Cart toEntity(CartDto cartDto);

    CartProductDto toCartProductDto(CartProduct cartProduct);

    CartProduct toEntity(CartProductDto productDto);

    List<CartDto> toCartDtoList(List<Cart> carts);

}
