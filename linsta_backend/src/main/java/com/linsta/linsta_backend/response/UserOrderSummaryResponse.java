package com.linsta.linsta_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderSummaryResponse {
    private long totalOrders;
    private int totalQuantity;
    private long totalAmount;
}
