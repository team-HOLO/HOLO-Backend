//package com.elice.holo.order.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.anyList;
//import static org.mockito.Mockito.anyLong;
//import static org.mockito.Mockito.anyString;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.elice.holo.member.domain.Member;
//import com.elice.holo.member.repository.MemberRepository;
//import com.elice.holo.order.domain.Order;
//import com.elice.holo.order.domain.OrderStatus;
//import com.elice.holo.order.dto.OrderDto;
//import com.elice.holo.order.dto.OrderMapper;
//import com.elice.holo.order.dto.OrderProductDto;
//import com.elice.holo.order.repository.OrderRepository;
//import com.elice.holo.product.domain.Product;
//import com.elice.holo.product.dto.AddProductResponse;
//import com.elice.holo.product.repository.ProductRepository;
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//
//    @InjectMocks
//    private OrderService orderService;
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private OrderMapper orderMapper;
//
//    private Member member;
//    private OrderDto orderDto;
//    private Order order;
//    private Product product;
//
//    @BeforeEach
//    void setUp() {
//        member = Member.builder()
//            .memberId(1L)
//            .email("test@test.com")
//            .password("password")
//            .name("Test User")
//            .isAdmin(false)
//            .isDeleted(false)
//            .tel("010-1234-5678")
//            .gender(true)
//            .age(30)
//            .build();
//
//        product = Product.createProduct("Test Product", 1000, "Test Description", 100);
//
//        orderDto = new OrderDto();
//        orderDto.setTotalPrice(BigDecimal.valueOf(1000));
//        orderDto.setShippingAddress("123 test test");
//        orderDto.setOrderProducts(Collections.singletonList(new OrderProductDto(1L, 1L, 2)));
//
//        order = Order.builder()
//            .orderId(1L)
//            .member(member)
//            .status(OrderStatus.ORDER)
//            .totalPrice(BigDecimal.valueOf(1000))
//            .shippingAddress("123 Test test")
//            .orderProducts(Collections.emptyList())
//            .build();
//    }
//
//    @Test
//    void createOrder() {
//        // Given
//        when(memberRepository.findByEmailAndIsDeletedFalse(anyString())).thenReturn(
//            Optional.of(member));
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
//        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
//            Order savedOrder = invocation.getArgument(0);
//            return Order.builder()
//                .orderId(1L)
//                .member(savedOrder.getMember())
//                .status(savedOrder.getStatus())
//                .totalPrice(savedOrder.getTotalPrice())
//                .shippingAddress(savedOrder.getShippingAddress())
//                .orderProducts(savedOrder.getOrderProducts())
//                .build();
//        });
//
//        // When
//        Long orderId = orderService.createOrder(orderDto, "test@test.com");
//
//        // Then
//        assertNotNull(orderId);
//        assertEquals(1L, orderId);  // 새로운 리턴 값에 대한 검증
//        System.out.println("주문 생성 테스트 통과: Order ID = " + orderId);
//        verify(orderRepository, times(1)).save(any(Order.class));
//    }
//
//    @Test
//    void getOrderList() {
//        // Given
//        when(memberRepository.findByEmailAndIsDeletedFalse(anyString())).thenReturn(
//            Optional.of(member));
//        when(orderRepository.findByMember_MemberId(anyLong())).thenReturn(
//            Collections.singletonList(order));
//        when(orderMapper.toOrderDto(any(Order.class))).thenReturn(orderDto);
//
//        // When
//        List<OrderDto> orderList = orderService.getOrderList("test@test.com");
//
//        // Then
//        assertNotNull(orderList);
//        assertEquals(1, orderList.size());
//        assertEquals(orderDto, orderList.get(0));  // DTO 내용 비교
//        System.out.println("주문 목록 조회 테스트 통과: 주문 수 = " + orderList.size());
//    }
//
//    @Test
//    void getAllOrders() {
//        // Given
//        when(orderRepository.findAllOrders()).thenReturn(Collections.singletonList(order));
//        when(orderMapper.toOrderDto(any(Order.class))).thenReturn(orderDto);
//
//        // When
//        List<OrderDto> allOrders = orderService.getAllOrders();
//
//        // Then
//        assertNotNull(allOrders);
//        assertEquals(1, allOrders.size());
//        assertEquals(orderDto, allOrders.get(0));  // DTO 내용 비교
//        System.out.println("모든 주문 조회 테스트 통과: 주문 수 = " + allOrders.size());
//    }
//
//    @Test
//    void updateOrder() {
//        // Given
//        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
//
//        // When
//        orderService.updateOrder(1L, orderDto);
//
//        // Then
//        verify(orderRepository, times(1)).save(any(Order.class));
//        System.out.println("주문 업데이트 테스트 통과");
//    }
//
//    @Test
//    void cancelOrder() {
//        // Given
//        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
//
//        // When
//        orderService.cancelOrder(1L);
//
//        // Then
//        verify(orderRepository, times(1)).save(any(Order.class));
//        System.out.println("주문 취소 테스트 통과");
//    }
//
//    @Test
//    void getProductsId() {
//        // Given
//        when(productRepository.findAllById(anyList())).thenReturn(
//            Collections.singletonList(product));
//
//        // When
//        List<AddProductResponse> products = orderService.getProductsId(
//            Collections.singletonList(1L));
//
//        // Then
//        assertNotNull(products);
//        assertEquals(1, products.size());
//        System.out.println("상품 ID 조회 테스트 통과: 상품 수 = " + products.size());
//    }
//}
