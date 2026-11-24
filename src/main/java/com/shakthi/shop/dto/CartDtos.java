package com.shakthi.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CartDtos {

    public record CartItemRequest(
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity) {
    }

    public record CartItemResponse(
            Long id, Long productId, String productName, BigDecimal price, Integer quantity) {
    }
}