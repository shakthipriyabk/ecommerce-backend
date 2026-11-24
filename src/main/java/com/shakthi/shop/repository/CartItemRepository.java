package com.shakthi.shop.repository;

import com.shakthi.shop.entity.CartItem;
import com.shakthi.shop.entity.Product;
import com.shakthi.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}