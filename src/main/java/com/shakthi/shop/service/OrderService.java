package com.shakthi.shop.service;

import com.shakthi.shop.dto.OrderDtos.OrderItemResponse;
import com.shakthi.shop.dto.OrderDtos.OrderResponse;
import com.shakthi.shop.entity.*;
import com.shakthi.shop.exception.BadRequestException;
import com.shakthi.shop.exception.ResourceNotFoundException;
import com.shakthi.shop.repository.CartItemRepository;
import com.shakthi.shop.repository.OrderRepository;
import com.shakthi.shop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public OrderResponse placeOrder(String email) {
        User user = getUser(email);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            if (p.getStock() < ci.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + p.getName());
            }
            p.setStock(p.getStock() - ci.getQuantity());

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            order.getItems().add(oi);

            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setTotal(total);

        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String email) {
        return orderRepository.findByUserOrderByCreatedAtDesc(getUser(email)).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse cancel(String email, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Order not found");
        }
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.PAID) {
            throw new BadRequestException("Order cannot be cancelled in status " + order.getStatus());
        }
        for (OrderItem oi : order.getItems()) {
            Product p = oi.getProduct();
            p.setStock(p.getStock() + oi.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(order);
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProduct().getName(), i.getQuantity(), i.getPrice()))
                .toList();
        return new OrderResponse(o.getId(), o.getStatus().name(), o.getTotal(), o.getCreatedAt(), items);
    }
}