package com.elice.holo.order.service;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.repository.MemberRepository;
import com.elice.holo.order.domain.Order;
import com.elice.holo.order.domain.OrderProduct;
import com.elice.holo.order.domain.OrderStatus;
import com.elice.holo.order.dto.OrderDto;
import com.elice.holo.order.dto.OrderMapper;
import com.elice.holo.order.repository.OrderRepository;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.dto.AddProductResponse;
import com.elice.holo.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    // 주문 생성
    @Transactional
    public Long createOrder(OrderDto orderDto, String email) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(email)
            .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        Order order = Order.builder()
            .member(member)
            .status(OrderStatus.ORDER)
            .totalPrice(orderDto.getTotalPrice())
            .shippingAddress(orderDto.getShippingAddress())
            .build();

        List<OrderProduct> orderProducts = orderDto.getOrderProducts().stream()
            .map(dto -> {
                Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));
                return OrderProduct.builder()
                    .order(order)
                    .product(product)
                    .count(dto.getCount())
                    .build();
            })
            .collect(Collectors.toList());

        order.addOrderProducts(orderProducts);
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getOrderId();
    }

    // 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderDto> getOrderList(String email) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(email)
            .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        List<Order> orders = orderRepository.findByMember_MemberId(member.getMemberId());
        return orders.stream()
            .map(orderMapper::toOrderDto)
            .collect(Collectors.toList());
    }

    // 관리자가 모든 회원의 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAllOrders();
        return orders.stream()
            .map(orderMapper::toOrderDto)
            .collect(Collectors.toList());
    }

    // 배송지 수정
    @Transactional
    public void updateOrder(Long orderId, OrderDto updatedOrderDto) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));
        if (order.getStatus() != OrderStatus.ORDER) {
            throw new IllegalStateException("배송 중이거나 배달 완료된 주문은 수정할 수 없습니다.");
        }
        order.updateShippingAddress(updatedOrderDto.getShippingAddress());
        orderRepository.save(order);
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalStateException("해당 주문을 찾을 수 없습니다."));
        if (order.getStatus() != OrderStatus.ORDER) {
            throw new IllegalStateException("주문 상태가 'ORDER'일 때만 취소할 수 있습니다.");
        }
        order.updateOrderStatus(OrderStatus.CANCEL);
        orderRepository.save(order);
    }

    // 상품 정보 조회
    @Transactional(readOnly = true)
    public List<AddProductResponse> getProductsId(List<Long> productIds) {
        return productRepository.findAllById(productIds)
            .stream()
            .map(AddProductResponse::new)
            .collect(Collectors.toList());
    }
}
