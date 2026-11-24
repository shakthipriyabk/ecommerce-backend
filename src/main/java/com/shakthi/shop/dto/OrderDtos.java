package com.shakthi.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    public record OrderItemResponse(String productName, Integer quantity, BigDecimal price) {
    }

    public record OrderResponse(
            Long id, String status, BigDecimal total, LocalDateTime createdAt,
            List<OrderItemResponse> items) {
    }
}