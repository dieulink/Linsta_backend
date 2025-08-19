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

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    long countOrdersByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(o.quantity) FROM Order o WHERE o.user.id = :userId")
    Integer sumQuantityByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.user.id = :userId")
    Long sumTotalPriceByUserId(@Param("userId") Long userId);

    @Query(value = """
        SELECT 
            u.id AS userId, 
            u.name AS userName,
            COUNT(DISTINCT o.id) AS orderCount,
            COALESCE(SUM(od.quantity), 0) AS productCount,
            COALESCE(SUM(o.total_price), 0) AS totalPrice
        FROM users u
        LEFT JOIN orders o 
            ON u.id = o.user_id 
            AND MONTH(o.created_at) = :month
            AND YEAR(o.created_at) = :year
        LEFT JOIN order_detail od 
            ON o.id = od.order_id
        GROUP BY u.id, u.name
        ORDER BY totalPrice DESC
    """, nativeQuery = true)
    List<Object[]> getUserPurchaseStats(@Param("month") int month, @Param("year") int year);

    @Query(value = """
        SELECT
            u.id AS userId, 
            u.name AS userName,
            COUNT(DISTINCT o.id) AS orderCount,
            COALESCE(SUM(od.quantity), 0) AS productCount,
            COALESCE(SUM(o.total_price), 0) AS totalPrice
        FROM users u
        LEFT JOIN orders o ON u.id = o.user_id
        LEFT JOIN order_detail od ON o.id = od.order_id
        AND o.created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
        GROUP BY u.id, u.name
        ORDER BY totalPrice DESC
        """, nativeQuery = true)
    List<Object[]> getRevenueLast6Months();

    @Query(value = """
    SELECT 
        u.id AS userId, 
        u.name AS userName,
        COUNT(DISTINCT o.id) AS orderCount,
        COALESCE(SUM(od.quantity), 0) AS productCount,
        COALESCE(SUM(o.total_price), 0) AS totalPrice
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    LEFT JOIN order_detail od ON o.id = od.order_id
    AND YEAR(o.created_at) = YEAR(CURDATE())
    GROUP BY u.id, u.name
    ORDER BY totalPrice DESC
    """, nativeQuery = true)
    List<Object[]> getRevenueCurrentYear();
    
    @Query(value = """
        SELECT 
            p.id AS productId,
            p.name AS productName,
            p.image_url AS imageUrl,
            COALESCE(SUM(od.quantity), 0) AS totalSold,
            COALESCE(SUM(od.quantity * od.product_price), 0) AS totalRevenue
        FROM products p
        LEFT JOIN order_detail od 
            ON p.id = od.product_id
        LEFT JOIN orders o 
            ON od.order_id = o.id 
            AND MONTH(o.created_at) = :month
            AND YEAR(o.created_at) = :year
        GROUP BY p.id, p.name, p.image_url
        ORDER BY totalRevenue DESC
        """, nativeQuery = true)
    List<Object[]> getProductRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query(value = """
        SELECT 
            p.id AS productId,
            p.name AS productName,
            p.image_url AS imageUrl,
            COALESCE(SUM(od.quantity), 0) AS totalSold,
            COALESCE(SUM(od.quantity * od.product_price), 0) AS totalRevenue
        FROM products p
        LEFT JOIN order_detail od 
            ON p.id = od.product_id
        LEFT JOIN orders o 
            ON od.order_id = o.id 
            AND o.created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
        GROUP BY p.id, p.name, p.image_url
        ORDER BY totalRevenue DESC
        """, nativeQuery = true)
    List<Object[]> getProductRevenueLast6Months();

    @Query(value = """
        SELECT 
            p.id AS productId,
            p.name AS productName,
            p.image_url AS imageUrl,
            COALESCE(SUM(od.quantity), 0) AS totalSold,
            COALESCE(SUM(od.quantity * od.product_price), 0) AS totalRevenue
        FROM products p
        LEFT JOIN order_detail od 
            ON p.id = od.product_id
        LEFT JOIN orders o 
            ON od.order_id = o.id 
            AND YEAR(o.created_at) = YEAR(CURDATE())
        GROUP BY p.id, p.name, p.image_url
        ORDER BY totalRevenue DESC
        """, nativeQuery = true)
    List<Object[]> getProductRevenueCurrentYear();
}
