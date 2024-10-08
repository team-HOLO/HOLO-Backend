package com.elice.holo.order.domain;

import com.elice.holo.common.BaseEntity;
import com.elice.holo.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@NoArgsConstructor
@Getter
@Table(name = "orders") // 테이블 이름을 orders로 변경하여 SQL 예약어 충돌 해결
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY) // 한 명의 회원은 여러개 주문을 할 수 있다
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts; // 주문 상품 리스트

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status; // OrderStatus는 enum으로 관리

    @Column(nullable = false)
    private LocalDateTime orderDate; // 주문 날짜

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice; // 총 가격

    @Column(nullable = false)
    private String shippingAddress; // 배송 주소

    @Builder
    public Order(Long orderId, Member member, List<OrderProduct> orderProducts,
        OrderStatus status, LocalDateTime orderDate, BigDecimal totalPrice,
        String shippingAddress) {
        this.orderId = orderId;
        this.member = member;
        this.orderProducts = orderProducts != null ? orderProducts : new ArrayList<>();
        this.status = status;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.shippingAddress = shippingAddress;
    }

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now(); // 주문 생성 시 현재 시간으로 설정
    }

    // 주문 상태 변경 메소드
    public void updateOrderStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    // 총 가격 업데이트 메소드
    public void updateTotalPrice(BigDecimal newTotalPrice) {
        this.totalPrice = newTotalPrice;
    }

    // 배송 주소 업데이트 메소드
    public void updateShippingAddress(String newAddress) {
        this.shippingAddress = newAddress;
    }

    public void addOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts.clear();
        this.orderProducts.addAll(orderProducts);
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrder(this);
        }
    }


}
