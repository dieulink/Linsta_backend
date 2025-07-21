package com.linsta.linsta_backend.repository;

import com.linsta.linsta_backend.model.CartItem;
import com.linsta.linsta_backend.model.Category;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findCartByUserandPrduct(Long productId, Long userId);

    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.user.id = :userId")
    Integer getTotalProductQuantityByUserId(@Param("userId") Long userId);

}
