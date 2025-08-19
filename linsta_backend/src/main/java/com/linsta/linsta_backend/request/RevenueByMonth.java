package com.linsta.linsta_backend.request;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueByMonth {
    private Long userId;
    private String userName;
    private Long orderCount;
    private Long productCount;
    private Double totalPrice;
}
