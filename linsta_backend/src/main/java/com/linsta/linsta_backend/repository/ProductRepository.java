package com.linsta.linsta_backend.repository;

import com.linsta.linsta_backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);

    @Override
    Optional<Product> findById(Long id);

    Page<Product> findByCategoryId(int categoryId, Pageable pageable);

    Page<Product> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByCategoryIdAndNameContainingIgnoreCase(int categoryId, String keyword, Pageable pageable);


}
