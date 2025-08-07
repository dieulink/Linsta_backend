package com.linsta.linsta_backend.controller;

import com.linsta.linsta_backend.request.OrderRequest;
import com.linsta.linsta_backend.response.OrderResponse;
import com.linsta.linsta_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/add_order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        try {
            orderService.createOrder(request);
            return ResponseEntity.ok("Tạo đơn hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Tạo đơn hàng thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/list_order/user")
    public ResponseEntity<?> getOrdersByUserId(@RequestParam("userId") Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
}
