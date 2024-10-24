package com.elice.holo.order.service;


import com.elice.holo.common.exception.ErrorCode;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.repository.MemberRepository;
import com.elice.holo.order.domain.Order;
import com.elice.holo.order.domain.OrderProduct;
import com.elice.holo.order.domain.OrderStatus;
import com.elice.holo.order.dto.OrderProductRequestDto;
import com.elice.holo.order.dto.OrderRequestDto;
import com.elice.holo.order.dto.OrderResponseDto;
import com.elice.holo.order.exception.OrderNotFoundException;
import com.elice.holo.order.repository.OrderProductRepository;
import com.elice.holo.order.repository.OrderRepository;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.exception.ProductNotFoundException;
import com.elice.holo.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;


    // 주문 생성
    @Transactional
    public Long createOrder(OrderRequestDto requestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        Long memberId = memberDetails.getMemberId();  // 로그인한 사용자의 memberId

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        List<OrderProduct> orderProducts = new ArrayList<>();

        for (OrderProductRequestDto productRequest : requestDto.getProducts()) {
            Product product = productRepository.findById(productRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다."));

            OrderProduct orderProduct = OrderProduct.createOrderProduct(
                product,
                productRequest.getQuantity(),
                productRequest.getColor(),
                productRequest.getSize()
            );

            orderProducts.add(orderProduct);
        }
        int totalPrice = calculateTotalPrice(orderProducts);

        Order order = Order.createOrder(member, totalPrice, requestDto.getShippingAddress(),
            requestDto.getShippingRequest(), requestDto.getRecipientName(),
            orderProducts);

        orderRepository.save(order);

        return order.getOrderId();
    }

    private int calculateTotalPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
            .mapToInt(op -> op.getProduct().getPrice() * op.getQuantity())
            .sum();
    }

    // 회원 주문 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMemberOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        Long memberId = memberDetails.getMemberId();

        List<Order> orders = orderRepository.findByMember_MemberIdAndIsDeletedFalse(memberId);
        return orders.stream()
            .map(OrderResponseDto::new)
            .collect(Collectors.toList());  // 주문 목록을 DTO로 변환하여 반환
    }

    // 관리자용 전체 주문 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAllByIsDeletedFalseOrderByOrderIdAsc();
        return orders.stream()
            .map(OrderResponseDto::new)
            .collect(Collectors.toList());
    }

    //회원 주문취소(상태가 ORDER일때만 가능)
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException(ErrorCode.ORDER_NOT_FOUND, "해당 주문을 찾을 수 없습니다."));
        if (order.getStatus() != OrderStatus.ORDER) {
            throw new IllegalStateException("주문을 취소할 수 없는 상태입니다.");
        }
        order.updateOrderStatus(OrderStatus.CANCEL);
        // 주문취소시 상품의 puantity다시 복구
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            orderProduct.getProduct().addStock(orderProduct.getQuantity());
        }
    }

    // 관리자 주문상태 변경
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException(ErrorCode.ORDER_NOT_FOUND, "해당 주문을 찾을 수 없습니다."));

        // 주문 상태 업데이트
        order.updateOrderStatus(newStatus);
        // 주문상태를 취소로 puantity다시 복구
        if (newStatus == OrderStatus.CANCEL) {
            for (OrderProduct orderProduct : order.getOrderProducts()) {
                orderProduct.getProduct().addStock(orderProduct.getQuantity());
            }
        }
    }

    // 주문 삭제 (소프트 딜리트 적용)
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException(ErrorCode.ORDER_NOT_FOUND, "해당 주문을 찾을 수 없습니다."));
        // 주문 상태가 CANCLE&FINISH 일때만 삭제가능
        if (order.getStatus() != OrderStatus.CANCEL && order.getStatus() != OrderStatus.FINISH) {
            throw new IllegalStateException("주문이 취소 상태가 아니므로 삭제할 수 없습니다.");
        }
        order.softDelete();
    }

    //주문 수정
    @Transactional
    public void updateShippingInfo(Long orderId, String newShippingAddress, String newRecipientName,
        String newShippingRequest) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(
                () -> new OrderNotFoundException(ErrorCode.ORDER_NOT_FOUND, "해당 주문을 찾을 수 없습니다."));

        order.updateShippingInfo(newShippingAddress, newRecipientName, newShippingRequest);
    }

}