package com.shakthi.shop.controller;

import com.shakthi.shop.dto.OrderDtos.OrderResponse;
import com.shakthi.shop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(Authentication auth) {
        return orderService.placeOrder(auth.getName());
    }

    @GetMapping
    public List<OrderResponse> myOrders(Authentication auth) {
        return orderService.myOrders(auth.getName());
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancel(Authentication auth, @PathVariable Long id) {
        return orderService.cancel(auth.getName(), id);
    }
}