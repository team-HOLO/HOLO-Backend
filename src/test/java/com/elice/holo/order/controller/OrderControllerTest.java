package com.elice.holo.order.controller;

import static com.elice.holo.order.domain.OrderStatus.CANCEL;
import static com.elice.holo.order.domain.OrderStatus.ORDER;
import static com.elice.holo.order.domain.OrderStatus.SHIPPING;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elice.holo.category.repository.CategoryRepository;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.repository.MemberRepository;
import com.elice.holo.order.domain.Order;
import com.elice.holo.order.dto.OrderProductRequestDto;
import com.elice.holo.order.dto.OrderRequestDto;
import com.elice.holo.order.dto.UpdateShippingInfoDto;
import com.elice.holo.order.repository.OrderRepository;
import com.elice.holo.order.service.DiscordWebhookService;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private DiscordWebhookService discordWebhookService; // DiscordWebhookService Mock

    @BeforeEach
    public void mockMvcSetUp() {
        Member member = createMember();
        setSecurityContext(member);
    }

    private final String url ="/api/orders";

    @Test
    @DisplayName("바로 주문하기(단일 주문) 테스트")
    public void createOrderTest() throws Exception {

        //given
        Product savedProduct = getProduct("데스커", 250000, "모션 데스크", 100);

        //상품 주문 정보(주문 수량, 옵션)
        OrderProductRequestDto orderProductRequestDto = getOrderProductRequestDto(
            savedProduct, "BROWN", "1200", 2);

        //주문 정보(배송지, 받는 사람, 배송 요청 사항)
        OrderRequestDto orderRequestDto = getOrderRequestDto(List.of(orderProductRequestDto),
            "홍길동", "인천시", "문앞에 놓아주세요");

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        //then
        result.andExpect(status().isCreated());

        String responseBody = result.andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readValue(responseBody, Long.class);

        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId).get();
        assertThat(order.getShippingAddress()).isEqualTo("인천시");
        assertThat(order.getOrderProducts().get(0).getColor()).isEqualTo("BROWN");
        assertThat(order.getOrderProducts().get(0).getSize()).isEqualTo("1200");

        assertThat(order.getOrderProducts().get(0).getProduct().getName()).isEqualTo("데스커");
        //상품 재고는 주문수량만큼 줄어들어야 함
        assertThat(order.getOrderProducts().get(0).getProduct().getStockQuantity()).isEqualTo(98);
    }

    @Test
    @DisplayName("장바구니 상품 주문(다수 주문) 테스트")
    public void cartOrderTest() throws Exception {

        //given
        Product product1 = getProduct("그랑데 건조기", 860000, "삼성전자 그랑데", 100);
        Product product2 = getProduct("머쉬룸 조명", 29800, "미드센츄리 모던 머쉬룸", 200);

        OrderProductRequestDto orderProductRequestDto1 = getOrderProductRequestDto(product1, "WHITE",
            "one size", 1);
        OrderProductRequestDto orderProductRequestDto2 = getOrderProductRequestDto(product2,
            "YELLOW", "M", 2);

        OrderRequestDto orderRequestDto = getOrderRequestDto(
            List.of(orderProductRequestDto1, orderProductRequestDto2),
            "홍길동", "광명시", "경비실에 맡겨주세요");

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        //then
        result.andExpect(status().isCreated());

        String responseBody = result.andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readValue(responseBody, Long.class);

        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId).get();
        assertThat(order.getOrderProducts().size()).isEqualTo(2);
        assertThat(order.getShippingAddress()).isEqualTo("광명시");
        assertThat(order.getOrderProducts().get(0).getColor()).isEqualTo("WHITE");
        assertThat(order.getOrderProducts().get(0).getSize()).isEqualTo("one size");
        assertThat(order.getOrderProducts().get(1).getColor()).isEqualTo("YELLOW");

    }

    @Test
    @DisplayName("주문 수량이 상품 재고보다 초과되면 productNotEnoughException 발생 test")
    public void productNotEnoughExceptionTest() throws Exception {

        //given
        Product savedProduct = getProduct("발렌", 12000, "발렌 2.0 조명", 2);

        //상품 주문 정보(주문 수량, 옵션)
        OrderProductRequestDto orderProductRequestDto = getOrderProductRequestDto(
            savedProduct, "BLACK", "M", 5);

        //주문 정보(배송지, 받는 사람, 배송 요청 사항)
        OrderRequestDto orderRequestDto = getOrderRequestDto(List.of(orderProductRequestDto),
            "홍길동", "서울시", "문앞에 놓아주세요");

        //when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        //then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Product not enough - 재고가 부족합니다."));
    }

    @Test
    @DisplayName("주문내역 조회 테스트")
    public void getMemberOrdersTest() throws Exception {

        //given
        Product product1 = getProduct("미닉스 미니 건조기", 300000, "미니 건조", 100);
        Product product2 = getProduct("달 조명", 32000, "3D 달 조명", 50);

        OrderProductRequestDto orderProductRequestDto1 = getOrderProductRequestDto(product1, "WHITE",
            "ONE SIZE", 1);
        OrderProductRequestDto orderProductRequestDto2 = getOrderProductRequestDto(product2, "WHITE",
            "ONE SIZE", 1);

        OrderRequestDto orderRequestDto = getOrderRequestDto(
            List.of(orderProductRequestDto1, orderProductRequestDto2),
            "홍길동", "마곡", "문앞에 놓아주세요");

        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        //when
        ResultActions result = mockMvc.perform(get(url));

        //then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].totalPrice").value(332000))
            .andExpect(jsonPath("$[0].shippingAddress").value("마곡"))
            .andExpect(jsonPath("$[0].status").value("ORDER"))
            .andExpect(jsonPath("$[0].orderProducts[0].productName").value("미닉스 미니 건조기"))
            .andExpect(jsonPath("$[0].orderProducts[1].productName").value("달 조명"))
            .andExpect(jsonPath("$[0].orderProducts[0].color").value("WHITE"));
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrderTest() throws Exception {

        //given
        Product savedProduct = getProduct("데스커", 250000, "모션 데스크", 100);

        //상품 주문 정보(주문 수량, 옵션)
        OrderProductRequestDto orderProductRequestDto = getOrderProductRequestDto(
            savedProduct, "BROWN", "1200", 2);

        //주문 정보(배송지, 받는 사람, 배송 요청 사항)
        OrderRequestDto orderRequestDto = getOrderRequestDto(List.of(orderProductRequestDto),
            "홍길동", "송도", "문앞에 놓아주세요");

        //주문
        ResultActions orderResult = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        String responseBody = orderResult.andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readValue(responseBody, Long.class);

        //when
        ResultActions result = mockMvc.perform(put(url + "/{orderId}/cancel", orderId));

        //then
        result.andExpect(status().isOk());

        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId).get();
        assertThat(order.getStatus()).isEqualTo(CANCEL);
    }

    @Test
    @DisplayName("주문 수정 테스트")
    public void updateOrderTest() throws Exception {

        //given
        Product savedProduct = getProduct("데스커", 250000, "모션 데스크", 100);

        //상품 주문 정보(주문 수량, 옵션)
        OrderProductRequestDto orderProductRequestDto = getOrderProductRequestDto(
            savedProduct, "BROWN", "1200", 2);

        //주문 정보(배송지, 받는 사람, 배송 요청 사항)
        OrderRequestDto orderRequestDto = getOrderRequestDto(List.of(orderProductRequestDto),
            "홍길동", "송도", "문앞에 놓아주세요");

        //주문
        ResultActions orderResult = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        String responseBody = orderResult.andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readValue(responseBody, Long.class);

        UpdateShippingInfoDto updateDto = new UpdateShippingInfoDto();
        updateDto.setRecipientName("홍길동");
        updateDto.setShippingAddress("마곡");
        updateDto.setShippingRequest("경비실에 맡겨주세요");

        //when
        ResultActions result = mockMvc.perform(put(url + "/{orderId}/update", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)));

        //then
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId).get();
        assertThat(order.getStatus()).isEqualTo(ORDER);
        assertThat(order.getShippingAddress()).isEqualTo("마곡");
        assertThat(order.getShippingRequest()).isEqualTo("경비실에 맡겨주세요");
    }

    @Test
    @DisplayName("배송 시작된 주문 취소시 IllegalStateException 테스트")
    public void OrderCancelExceptionTest() throws Exception {

        //given
        Product savedProduct = getProduct("데스커", 250000, "모션 데스크", 100);

        //상품 주문 정보(주문 수량, 옵션)
        OrderProductRequestDto orderProductRequestDto = getOrderProductRequestDto(
            savedProduct, "BROWN", "1200", 2);

        //주문 정보(배송지, 받는 사람, 배송 요청 사항)
        OrderRequestDto orderRequestDto = getOrderRequestDto(List.of(orderProductRequestDto),
            "홍길동", "송도", "문앞에 놓아주세요");

        //주문
        ResultActions orderResult = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        String responseBody = orderResult.andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readValue(responseBody, Long.class);
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId).get();
        order.updateOrderStatus(SHIPPING);

        //when
        ResultActions result = mockMvc.perform(put(url + "/{orderId}/cancel", orderId));

        //then
        result.andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Internal server error"));

    }

    @Test
    @DisplayName("주문 삭제 테스트")
    public void deleteOrderTest() throws Exception {

        //given
        Product savedProduct = getProduct("데스커", 250000, "모션 데스크", 100);

        //상품 주문 정보(주문 수량, 옵션)
        OrderProductRequestDto orderProductRequestDto = getOrderProductRequestDto(
            savedProduct, "BROWN", "1200", 2);

        //주문 정보(배송지, 받는 사람, 배송 요청 사항)
        OrderRequestDto orderRequestDto = getOrderRequestDto(List.of(orderProductRequestDto),
            "홍길동", "송도", "문앞에 놓아주세요");

        //주문
        ResultActions orderResult = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequestDto)));

        String responseBody = orderResult.andReturn().getResponse().getContentAsString();
        Long orderId = objectMapper.readValue(responseBody, Long.class);

        //when
        ResultActions result = mockMvc.perform(delete(url + "/{orderId}", orderId));

        //then
        Order order = orderRepository.findById(orderId).get();
        assertThat(order.isDeleted()).isTrue();
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

    private  void setSecurityContext(Member member) {
        MemberDetails memberDetails = new MemberDetails(member);

        SecurityContextHolder.setContext(new SecurityContextImpl(
            new UsernamePasswordAuthenticationToken(memberDetails, "password",
                memberDetails.getAuthorities())));
    }

}
