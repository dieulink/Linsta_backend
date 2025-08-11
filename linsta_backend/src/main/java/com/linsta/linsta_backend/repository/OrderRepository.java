package com.linsta.linsta_backend.repository;

import com.linsta.linsta_backend.model.Order;
import com.linsta.linsta_backend.response.DailyRevenueDTO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findByUserId(Long userId);
       Optional<Order> findById(Long orderId);
    @Query("SELECT COUNT(o) FROM Order o WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year")
    long countOrdersByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(o.quantity), 0) " +
            "FROM Order o " +
            "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year")
    long sumQuantityByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o " +
            "WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year")
    long sumTotalPriceByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT DATE(created_at) as date, SUM(total_price) as revenue " +
            "FROM orders " +
            "WHERE created_at >= :startDate " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY DATE(created_at)", nativeQuery = true)
    List<Object[]> findDailyRevenueSinceNative(@Param("startDate") LocalDate startDate);


}
