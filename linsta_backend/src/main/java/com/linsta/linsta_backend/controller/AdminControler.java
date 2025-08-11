package com.linsta.linsta_backend.controller;

import com.linsta.linsta_backend.model.Order;
import com.linsta.linsta_backend.model.User;
import com.linsta.linsta_backend.repository.OrderRepository;
import com.linsta.linsta_backend.repository.UserRepository;
import com.linsta.linsta_backend.response.DailyRevenueDTO;
import com.linsta.linsta_backend.response.OrderResponse;
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
}
