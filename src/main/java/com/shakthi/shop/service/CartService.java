package com.shakthi.shop.service;

import com.shakthi.shop.dto.CartDtos.CartItemRequest;
import com.shakthi.shop.dto.CartDtos.CartItemResponse;
import com.shakthi.shop.entity.CartItem;
import com.shakthi.shop.entity.Product;
import com.shakthi.shop.entity.User;
import com.shakthi.shop.exception.ResourceNotFoundException;
import com.shakthi.shop.repository.CartItemRepository;
import com.shakthi.shop.repository.ProductRepository;
import com.shakthi.shop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(String email) {
        return cartItemRepository.findByUser(getUser(email)).stream()
                .map(c -> new CartItemResponse(c.getId(), c.getProduct().getId(),
                        c.getProduct().getName(), c.getProduct().getPrice(), c.getQuantity()))
                .toList();
    }

    @Transactional
    public List<CartItemResponse> addItem(String email, CartItemRequest req) {
        User user = getUser(email);
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + req.productId()));
        CartItem item = cartItemRepository.findByUserAndProduct(user, product).orElseGet(() -> {
            CartItem c = new CartItem();
            c.setUser(user);
            c.setProduct(product);
            c.setQuantity(0);
            return c;
        });
        item.setQuantity(item.getQuantity() + req.quantity());
        cartItemRepository.save(item);
        return getCart(email);
    }

    @Transactional
    public void removeItem(String email, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        cartItemRepository.delete(item);
    }
}