package com.elice.holo.order.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elice.holo.category.repository.CategoryRepository;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.repository.MemberRepository;
import com.elice.holo.order.domain.Order;
import com.elice.holo.order.domain.OrderProduct;
import com.elice.holo.order.domain.OrderStatus;
import com.elice.holo.order.dto.OrderProductRequestDto;
import com.elice.holo.order.dto.OrderRequestDto;
import com.elice.holo.order.dto.UpdateOrderStatusDto;
import com.elice.holo.order.repository.OrderRepository;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AdminOrderControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();
    }

    private final String url ="/api/admin/orders";

    @Test
    @DisplayName("관리자 전체 주문내역 조회 테스트")
    public void getAllOrdersForAdminTest() throws Exception {

        //given
        Member member = createMember();

        Product product1 = getProduct("쇼파", 300000, "데일리", 100);
        Product product2 = getProduct("의자", 200000, "데일리", 100);

        OrderProduct orderProduct1 = OrderProduct.createOrderProduct(product1, 1, "BLACK", "L");
        OrderProduct orderProduct2 = OrderProduct.createOrderProduct(product2, 1, "WHITE",
            "one size");


        Order order = Order.createOrder(member, 500000, "김포", "홍길동",
            "문자 주세요", List.of(orderProduct1, orderProduct2));
        orderRepository.save(order);

        //when
        ResultActions result = mockMvc.perform(get(url));

        //then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].recipientName").value("홍길동"))
            .andExpect(jsonPath("$[0].totalPrice").value(500000))
            .andExpect(jsonPath("$[0].orderProducts[0].productName").value("쇼파"));
    }

    @Test
    @DisplayName("관리자 주문 취소 테스트")
    public void adminCancelOrderTest() throws Exception {

        //given
        Member member = createMember();

        Product product1 = getProduct("쇼파", 300000, "데일리", 100);
        Product product2 = getProduct("의자", 200000, "데일리", 100);

        OrderProduct orderProduct1 = OrderProduct.createOrderProduct(product1, 1, "BLACK", "L");
        OrderProduct orderProduct2 = OrderProduct.createOrderProduct(product2, 1, "WHITE",
            "one size");


        Order order = Order.createOrder(member, 500000, "김포", "홍길동",
            "문자 주세요", List.of(orderProduct1, orderProduct2));
        Order savedOrder = orderRepository.save(order);

        //when
        ResultActions result = mockMvc.perform(put(url + "/{orderId}/cancel", order.getOrderId()));
        //then
        result.andExpect(status().isOk());
        Assertions.assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("관리자 주문 상태 변경 테스트")
    public void updateOrderStatusTest() throws Exception {

        //given
        Member member = createMember();

        Product product1 = getProduct("쇼파", 300000, "데일리", 100);
        Product product2 = getProduct("의자", 200000, "데일리", 100);

        OrderProduct orderProduct1 = OrderProduct.createOrderProduct(product1, 1, "BLACK", "L");
        OrderProduct orderProduct2 = OrderProduct.createOrderProduct(product2, 1, "WHITE",
            "one size");


        Order order = Order.createOrder(member, 500000, "김포", "홍길동",
            "문자 주세요", List.of(orderProduct1, orderProduct2));
        Order savedOrder = orderRepository.save(order);

        UpdateOrderStatusDto updateOrderStatusDto = new UpdateOrderStatusDto();
        updateOrderStatusDto.setNewStatus(OrderStatus.SHIPPING);

        //when
        ResultActions result = mockMvc.perform(put(url + "/{orderId}/status", order.getOrderId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateOrderStatusDto)));

        //then
        result.andExpect(status().isOk());
        Assertions.assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }



    private OrderRequestDto getOrderRequestDto(List<OrderProductRequestDto> orderProductRequestDto,
        String recipientName, String shippingAddress, String shippingRequest) {
        OrderRequestDto orderRequestDto = new OrderRequestDto(orderProductRequestDto, shippingAddress, recipientName, shippingRequest);
        return orderRequestDto;
    }

    private OrderProductRequestDto getOrderProductRequestDto(Product savedProduct,
        String color, String size, int quantity) {
        OrderProductRequestDto orderProductRequestDto = new OrderProductRequestDto(
            savedProduct.getProductId(), quantity, color, size);
        return orderProductRequestDto;
    }

    private Product getProduct(String name, int price, String description, int stockQuantity) {
        Product product = Product.createProduct(name, price, description, stockQuantity);
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    private Member createMember() {
        Member member = Member.builder()
            .memberId(1L)
            .email("user@example.com")
            .password("password")
            .name("user")
            .isAdmin(false)
            .isDeleted(false)
            .tel("010-1234-5678")
            .age(30)
            .gender(true)
            .build();

        return memberRepository.save(member);
    }
}