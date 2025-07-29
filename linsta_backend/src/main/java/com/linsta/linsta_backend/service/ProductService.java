package com.linsta.linsta_backend.service;
import com.linsta.linsta_backend.model.Product;
import com.linsta.linsta_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> getProductsByCategory(int categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    public Page<Product> searchProductsByDescription(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByDescriptionContainingIgnoreCase(keyword, pageable);
    }

    public Page<Product> searchProductsByName(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    public Page<Product> searchProductsByNameandCategoryId(String keyword, int page, int id) {
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryIdAndNameContainingIgnoreCase(id,keyword, pageable);
    }
}
