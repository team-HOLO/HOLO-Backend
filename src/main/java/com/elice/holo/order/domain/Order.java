package com.elice.holo.order.domain;

import com.elice.holo.common.BaseEntity;
import com.elice.holo.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String shippingAddress;

    // private 생성자
    private Order(Member member, BigDecimal totalPrice, String shippingAddress,
        List<OrderProduct> orderProducts) {
        this.member = member;
        this.totalPrice = totalPrice;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.ORDER;
        this.orderProducts = new ArrayList<>(orderProducts);

        // 각 주문 상품에 현재 주문 연결
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrder(this);
        }
    }

    // 팩토리 메서드
    public static Order createOrder(Member member, BigDecimal totalPrice, String shippingAddress,
        List<OrderProduct> orderProducts) {
        return new Order(member, totalPrice, shippingAddress, orderProducts);
    }

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
    }

    // 상태 변경 메서드
    public void updateOrderStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    // 총 가격 업데이트
    public void updateTotalPrice(BigDecimal newTotalPrice) {
        this.totalPrice = newTotalPrice;
    }

    // 배송 주소 업데이트
    public void updateShippingAddress(String newAddress) {
        this.shippingAddress = newAddress;
    }

    // 주문 상품 추가 메서드
    public void addOrderProducts(List<OrderProduct> newOrderProducts) {
        this.orderProducts.clear();
        this.orderProducts.addAll(newOrderProducts);
        for (OrderProduct orderProduct : newOrderProducts) {
            orderProduct.setOrder(this);
        }
    }
}