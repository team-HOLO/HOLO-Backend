package com.elice.holo.order.dto;

import com.elice.holo.order.domain.Order;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "member.memberId", target = "memberId")
    OrderDto toOrderDto(Order order);

    @Mapping(source = "memberId", target = "member.memberId")
    Order toOrderEntity(OrderDto orderDto);

    List<OrderDto> toOrderDtoList(List<Order> orders);

    List<Order> toOrderEntityList(List<OrderDto> orderDtos);
}