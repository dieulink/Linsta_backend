package com.linsta.linsta_backend.repository;

import com.linsta.linsta_backend.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Long productId);
    List<Rating> findByProductIdAndUserId(Long productId, Long userId);

}
