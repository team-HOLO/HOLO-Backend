package com.elice.holo.order.service;

import com.elice.holo.common.exception.ErrorCode;
import com.elice.holo.order.domain.Order;
import com.elice.holo.order.dto.OrderRequestDto;
import com.elice.holo.order.exception.DiscordMessageFailedException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiscordWebhookService {

    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public DiscordWebhookService(RestTemplate restTemplate,
        @Value("${discord.webhook-url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    // 공통 알림 전송 메서드
    private void sendDiscordNotification(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = String.format("{\"content\":\"%s\"}", escapeJson(message));

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(webhookUrl, HttpMethod.POST, entity,
            String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new DiscordMessageFailedException(ErrorCode.DISCORD_MESSAGE_FAILED, "메세지 전송 실패");
        }
    }

    // 주문 생성 알림 전송
    public void sendOrderAddedNotification(OrderRequestDto orderRequestDto, String ordererName) {
        String productList = orderRequestDto.getProducts().stream()
            .map(product -> String.format("- 상품ID: %d, 수량: %d, 색상: %s, 사이즈: %s",
                product.getProductId(), product.getQuantity(), product.getColor(),
                product.getSize()))
            .collect(Collectors.joining("\n"));

        String message = String.format(
            "# 📦새로운 주문이 들어왔습니다!📦\n" +
                "```\n" +
                "주문자: %s\n" +
                "배송지: %s\n" +
                "수령인: %s\n" +
                "배송 요청사항: %s\n" +
                "```\n" +
                "## 주문 상품 목록\n" +
                "%s",
            ordererName, orderRequestDto.getShippingAddress(), orderRequestDto.getRecipientName(),
            orderRequestDto.getShippingRequest(), productList);

        sendDiscordNotification(message);
    }

    // 주문 수정 알림 전송
    public void sendOrderUpdatedNotification(Long orderId, String newShippingAddress,
        String newRecipientName, String newShippingRequest) {
        String message = String.format(
            "# ✏️주문 정보가 수정되었습니다!✏️\n" +
                "```\n" +
                "주문 ID: %d\n" +
                "새로운 배송지: %s\n" +
                "새로운 수령인: %s\n" +
                "새로운 배송 요청사항: %s\n" +
                "```\n",
            orderId, newShippingAddress, newRecipientName, newShippingRequest);

        sendDiscordNotification(message);
    }

    // 주문 취소 알림 전송
    public void sendOrderCanceledNotification(Order order) {
        String productList = order.getOrderProducts().stream()
            .map(orderProduct -> String.format("- 상품ID: %d, 수량: %d, 색상: %s, 사이즈: %s",
                orderProduct.getProduct().getProductId(), orderProduct.getQuantity(),
                orderProduct.getColor(), orderProduct.getSize()))
            .collect(Collectors.joining("\n"));

        String message = String.format(
            "# ❌ 주문이 취소되었습니다!\n" +
                "- 취소된 주문 ID: %d\n" +
                "## 취소된 상품 정보\n%s",
            order.getOrderId(), productList);

        sendDiscordNotification(message);
    }

    // 이스케이프 처리 함수
    private String escapeJson(String str) {
        return str.replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}