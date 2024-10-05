package com.elice.holo.order.domain;

import com.elice.holo.member.domain.Member;
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
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders") // 테이블 이름을 orders로 변경하여 SQL 예약어 충돌 해결
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY) // 한 명의 회원은 여러개 주문을 할 수 있다
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts; //주문 상품 리스트

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)//주문 상태
    private OrderStatus status; //OrderStatus는 enum으로 관리(확정x)

    @Column(nullable = false)
    private LocalDateTime orderDate; //주문 날짜

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt; //마지막 수정 시간

    @Column(nullable = false, precision = 19, scale = 2) // DECIMAL (소수점 2자리까지 19자리 숫자 저장가능)
    private BigDecimal totalPrice; // 총 가격


    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now(); // 주문 할떄 현재 시간으로 설정
    }


}
