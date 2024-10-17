package com.elice.holo.order.dto;

import com.elice.holo.member.domain.Member;
import com.elice.holo.order.domain.Order;
import com.elice.holo.order.domain.OrderProduct;
import com.elice.holo.product.domain.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-18T03:09:59+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDto toOrderDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setMemberId( orderMemberMemberId( order ) );
        orderDto.setOrderId( order.getOrderId() );
        orderDto.setOrderProducts( orderProductListToOrderProductDtoList( order.getOrderProducts() ) );
        orderDto.setShippingAddress( order.getShippingAddress() );
        orderDto.setOrderDate( order.getOrderDate() );
        orderDto.setStatus( order.getStatus() );
        orderDto.setTotalPrice( order.getTotalPrice() );

        return orderDto;
    }

    @Override
    public OrderProductDto toOrderProductDto(OrderProduct orderProduct) {
        if ( orderProduct == null ) {
            return null;
        }

        Long productId = null;
        Long orderProductId = null;
        int count = 0;

        productId = orderProductProductProductId( orderProduct );
        orderProductId = orderProduct.getOrderProductId();
        count = orderProduct.getCount();

        OrderProductDto orderProductDto = new OrderProductDto( orderProductId, productId, count );

        return orderProductDto;
    }

    @Override
    public Order toOrder(OrderDto orderDto, Member member) {
        if ( orderDto == null && member == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        if ( orderDto != null ) {
            order.orderId( orderDto.getOrderId() );
            order.status( orderDto.getStatus() );
            order.orderDate( orderDto.getOrderDate() );
            order.totalPrice( orderDto.getTotalPrice() );
            order.shippingAddress( orderDto.getShippingAddress() );
        }
        order.member( member );

        return order.build();
    }

    private Long orderMemberMemberId(Order order) {
        if ( order == null ) {
            return null;
        }
        Member member = order.getMember();
        if ( member == null ) {
            return null;
        }
        Long memberId = member.getMemberId();
        if ( memberId == null ) {
            return null;
        }
        return memberId;
    }

    protected List<OrderProductDto> orderProductListToOrderProductDtoList(List<OrderProduct> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderProductDto> list1 = new ArrayList<OrderProductDto>( list.size() );
        for ( OrderProduct orderProduct : list ) {
            list1.add( toOrderProductDto( orderProduct ) );
        }

        return list1;
    }

    private Long orderProductProductProductId(OrderProduct orderProduct) {
        if ( orderProduct == null ) {
            return null;
        }
        Product product = orderProduct.getProduct();
        if ( product == null ) {
            return null;
        }
        Long productId = product.getProductId();
        if ( productId == null ) {
            return null;
        }
        return productId;
    }
}
