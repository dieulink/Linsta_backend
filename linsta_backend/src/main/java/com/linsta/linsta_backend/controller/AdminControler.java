package com.linsta.linsta_backend.controller;

import com.linsta.linsta_backend.model.Order;
import com.linsta.linsta_backend.model.User;
import com.linsta.linsta_backend.repository.OrderRepository;
import com.linsta.linsta_backend.repository.UserRepository;
import com.linsta.linsta_backend.request.RevenueByMonth;
import com.linsta.linsta_backend.response.DailyRevenueDTO;
import com.linsta.linsta_backend.response.OrderResponse;
import com.linsta.linsta_backend.response.ProductRevenueResponse;
import com.linsta.linsta_backend.response.UserOrderSummaryResponse;
import com.linsta.linsta_backend.service.OrderService;
import com.linsta.linsta_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminControler {
    @Autowired
    OrderService orderService;

   @Autowired
    UserService userService;

   @Autowired
    OrderRepository orderRepository;

   @Autowired
    UserRepository userRepository;
    @GetMapping("/this_month")
    public ResponseEntity<Map<String, Object>> getOrderGrowthThisMonth() {
        Map<String, Object> result1 = orderService.getOrderGrowthThisMonth();
        Map<String, Object> result2 = userService.getUserGrowthThisMonth();
        result1.putAll(result2);
        return ResponseEntity.ok(result1);
    }
    @GetMapping("/revenue7day")
    public List<DailyRevenueDTO> getLast7DaysRevenue() {
        return orderService.get7DayRevenue();
    }
    @GetMapping("/list_order")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
    @PostMapping("/confirm_order/{orderId}")
    public ResponseEntity<String> updateStatusToShipping(@PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Đơn hàng không tồn tại");
        }

        Order order = optionalOrder.get();
        order.setStatus("Đang vận chuyển");
        orderRepository.save(order);

        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }
    @GetMapping("/list_user")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    @GetMapping("/summary/{userId}")
    public ResponseEntity<UserOrderSummaryResponse> getUserOrderSummary(@PathVariable Long userId) {
        UserOrderSummaryResponse summary = orderService.getUserOrderSummary(userId);
        return ResponseEntity.ok(summary);
    }
    @GetMapping("/user/revenue_by_month")
    public ResponseEntity<List<RevenueByMonth>> getUserPurchaseStats(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(orderService.getUserStatsByMonthYear(month, year));
    }
    @GetMapping("/user/revenue_6_month")
    public ResponseEntity<List<RevenueByMonth>> getRevenueLast6Months() {
        return ResponseEntity.ok(orderService.getUserRevenueLast6Months());
    }
    @GetMapping("/user/revenue_current_year")
    public ResponseEntity<List<RevenueByMonth>> getRevenueCurrentYear() {
        return ResponseEntity.ok(orderService.getUserRevenueCurentYear());
    }
    @GetMapping("/product/revenue_by_month")
    public List<ProductRevenueResponse> getProductRevenueByMonth(
            @RequestParam int month,
            @RequestParam int year) {
        return orderService.getProductRevenueByMonth(month, year);
    }

    @GetMapping("/product/revenue_6_month")
    public List<ProductRevenueResponse> getProductRevenueLast6Months() {
        return orderService.getProductRevenueLast6Months();
    }

    @GetMapping("/product/revenue_current_year")
    public List<ProductRevenueResponse> getProductRevenueCurrentYear() {
        return orderService.getProductRevenueCurrentYear();
    }
}
