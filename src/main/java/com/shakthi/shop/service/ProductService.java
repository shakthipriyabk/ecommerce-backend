package com.shakthi.shop.service;

import com.shakthi.shop.dto.ProductRequest;
import com.shakthi.shop.dto.ProductResponse;
import com.shakthi.shop.entity.Product;
import com.shakthi.shop.exception.ResourceNotFoundException;
import com.shakthi.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        copyToEntity(request, product);
        return toResponse(productRepository.save(product));
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        copyToEntity(request, product);
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        productRepository.delete(findOrThrow(id));
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    private void copyToEntity(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock());
    }
}