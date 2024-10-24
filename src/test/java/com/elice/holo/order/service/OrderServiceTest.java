package com.elice.holo.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderProductRepository orderProductRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrderTest() {

        //given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);  // SecurityContext 수동 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);

        MemberDetails memberDetails = mock(MemberDetails.class);
        when(authentication.getPrincipal()).thenReturn(memberDetails);
        when(memberDetails.getMemberId()).thenReturn(1L);  // 임의의 memberId 반환

        Member member = Member.builder().build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Product product = Product.createProduct("의자", 100000, "상품 설명", 50);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        List<OrderProductRequestDto> products = List.of(new OrderProductRequestDto(1L, 2, "blue", "L"));
        OrderRequestDto request = new OrderRequestDto(products, "서울", "배송 메모", "수신자 이름");


        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(OrderProduct.createOrderProduct(product, 2, "blue", "L"));


        Order unsavedOrder = Order.createOrder(member, 100000, "서울", "수신자 이름", "배송 메모", orderProducts);


        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);

            Field orderIdField = Order.class.getDeclaredField("orderId");
            orderIdField.setAccessible(true);
            orderIdField.set(order, 1L);
            return order;
        });

        // when
        Long orderId = orderService.createOrder(request);

        // then
        assertEquals(1L, orderId);
    }





    @Test
    @DisplayName("회원 주문 조회 테스트")
    void getMemberOrdersTest() {

        //given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);  // SecurityContext 수동 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);

        MemberDetails memberDetails = mock(MemberDetails.class);
        when(authentication.getPrincipal()).thenReturn(memberDetails);
        when(memberDetails.getMemberId()).thenReturn(1L);  // 임의의 memberId 반환

        Member member = Member.builder().build();

        Product product = Product.createProduct("의자", 100000, "상품 설명", 50);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(OrderProduct.createOrderProduct(product, 2, "blue", "L"));

        Order order = Order.createOrder(member, 200000, "서울", "수신자 이름", "배송 메모", orderProducts);


        when(orderRepository.findByMember_MemberIdAndIsDeletedFalse(1L))
                .thenReturn(List.of(order));

        //when
        List<OrderResponseDto> responseDtos = orderService.getMemberOrders();

        //then
        assertEquals(1, responseDtos.size());  // Expecting 1 order in the response
        assertEquals("수신자 이름", responseDtos.get(0).getRecipientName());
        assertEquals("서울", responseDtos.get(0).getShippingAddress());
        assertEquals(200000, responseDtos.get(0).getTotalPrice());
    }

    @Test
    @DisplayName("주문 취소 테스트")
    void cancelOrderTest() {

        //given
        Order order = Order.createOrder(Member.builder().build(), 100000, "주소", "요청", "수신자", new ArrayList<>());
        order.updateOrderStatus(OrderStatus.ORDER);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //when
        orderService.cancelOrder(1L);

        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("주문이 ORDER 상태가 아니면 취소 불가")
    void cancelOrderInvalidStateTest() {

        //given
        Order order = Order.createOrder(Member.builder().build(), 100000, "주소", "요청", "수신자", new ArrayList<>());
        order.updateOrderStatus(OrderStatus.SHIPPING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder(1L);
        });

        //then
        assertThat(exception.getMessage()).contains("주문을 취소할 수 없는 상태입니다.");
    }

    @Test
    @DisplayName("주문 삭제 테스트")
    void deleteOrderTest() {

        //given
        Order order = mock(Order.class);
        when(orderRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(order));

        //when
        orderService.deleteOrder(1L);

        //then
        verify(order, times(1)).softDelete();
    }

    @Test
    @DisplayName("주문이 존재하지 않으면 OrderNotFoundException 발생")
    void deleteOrderNotFoundExceptionTest() {

        //given
        when(orderRepository.findByOrderIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        //when
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.deleteOrder(1L);
        });

        //then
        assertThat(exception.getMessage()).contains("해당 주문을 찾을 수 없습니다.");
    }
    @Test
    @DisplayName("관리자 주문 취소 테스트")
    void adminCancelOrderTest() {

        //given
        Order order = Order.createOrder(Member.builder().build(), 100000, "주소", "요청", "수신자", new ArrayList<>());
        order.updateOrderStatus(OrderStatus.SHIPPING);  // This could be any status

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //when
        orderService.adminCancelOrder(1L);

        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }
    @Test
    @DisplayName("주문 상태 업데이트 테스트")
    void updateOrderStatusTest() {

        //given
        Order order = Order.createOrder(Member.builder().build(), 100000, "주소", "요청", "수신자", new ArrayList<>());
        order.updateOrderStatus(OrderStatus.ORDER);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //when
        orderService.updateOrderStatus(1L, OrderStatus.SHIPPING);

        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }
    @Test
    @DisplayName("관리자 전체 주문 조회 테스트")
    void getAllOrdersForAdminTest() {

        //given
        Member member = Member.builder().build();
        Product product = Product.createProduct("의자", 100000, "상품 설명", 50);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(OrderProduct.createOrderProduct(product, 2, "blue", "L"));

        Order order = Order.createOrder(member, 200000, "서울", "수신자 이름", "배송 메모", orderProducts);

        when(orderRepository.findAllByIsDeletedFalseOrderByOrderIdAsc()).thenReturn(List.of(order));

        //when
        List<OrderResponseDto> responseDtos = orderService.getAllOrdersForAdmin();

        //then
        assertEquals(1, responseDtos.size());  // Expecting 1 order in the response
        assertEquals("수신자 이름", responseDtos.get(0).getRecipientName());
        assertEquals(200000, responseDtos.get(0).getTotalPrice());
    }
    @Test
    @DisplayName("주문 배송 정보 수정 테스트")
    void updateShippingInfoTest() {

        //given
        Order order = Order.createOrder(Member.builder().build(), 100000, "주소", "수신자", "배송 메모", new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        //when
        orderService.updateShippingInfo(1L, "새 주소", "새 수신자", "새 요청");

        //then
        assertThat(order.getShippingAddress()).isEqualTo("새 주소");
        assertThat(order.getRecipientName()).isEqualTo("새 수신자");
        assertThat(order.getShippingRequest()).isEqualTo("새 요청");
    }
    @Test
    @DisplayName("주문이 없을 때 OrderNotFoundException 발생 테스트")
    void cancelOrderNotFoundExceptionTest() {

        //given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.cancelOrder(1L);
        });

        //then
        assertThat(exception.getMessage()).contains("해당 주문을 찾을 수 없습니다.");
    }





}
