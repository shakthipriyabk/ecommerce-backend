package com.shakthi.shop.controller;

import com.shakthi.shop.dto.CartDtos.CartItemRequest;
import com.shakthi.shop.dto.CartDtos.CartItemResponse;
import com.shakthi.shop.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartItemResponse> getCart(Authentication auth) {
        return cartService.getCart(auth.getName());
    }

    @PostMapping("/items")
    public List<CartItemResponse> addItem(Authentication auth, @Valid @RequestBody CartItemRequest req) {
        return cartService.addItem(auth.getName(), req);
    }

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(Authentication auth, @PathVariable Long id) {
        cartService.removeItem(auth.getName(), id);
    }
}