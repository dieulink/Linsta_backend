package com.linsta.linsta_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRevenueResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private Long totalSold;
    private Double totalRevenue;
}