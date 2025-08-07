package com.linsta.linsta_backend.repository;

import com.linsta.linsta_backend.model.Order;
import com.linsta.linsta_backend.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder(Order order);
}
