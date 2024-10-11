package com.elice.holo.order.dto;

import com.elice.holo.member.domain.Member;
import com.elice.holo.order.domain.Order;
import com.elice.holo.order.domain.OrderProduct;
import com.elice.holo.product.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Order 엔티티를 OrderDto로 매핑
    @Mapping(source = "member.memberId", target = "memberId")
    OrderDto toOrderDto(Order order);

    // OrderProduct 엔티티를 OrderProductDto로 매핑
    @Mapping(source = "product.productId", target = "productId")
    OrderProductDto toOrderProductDto(OrderProduct orderProduct);

    // OrderDto를 Order 엔티티로 매핑
    @Mapping(target = "orderProducts", ignore = true)
    Order toOrder(OrderDto orderDto, Member member);

    // OrderProductDto를 OrderProduct 엔티티로 매핑
    @Mapping(target = "order", ignore = true)
    default OrderProduct toOrderProduct(OrderProductDto orderProductDto, Order order,
        Product product) {
        return OrderProduct.createOrderProduct(order, product, orderProductDto.getCount());
    }
}
