package com.elice.holo.order.domain;

public enum OrderStatus {
    ORDER,  // 주문 완료
    SHIPPING, //배송중
    FINISH,  // 배송 완료
    CANCEL   // 주문 취소됨
}
