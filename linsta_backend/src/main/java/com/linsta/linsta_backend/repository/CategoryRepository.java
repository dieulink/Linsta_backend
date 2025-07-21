package com.linsta.linsta_backend.repository;

import com.linsta.linsta_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
