package com.linsta.linsta_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private int quantity;
    private Long totalPrice;
    private String receiveAddress;
    private String receiveName;
    private String receivePhone;
    private int shipCost;
    private String status;
    private Long payMethodId;
    private List<OrderDetailResponse> items;
}
