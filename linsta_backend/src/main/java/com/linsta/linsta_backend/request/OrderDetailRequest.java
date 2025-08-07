package com.linsta.linsta_backend.request;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Long productId;
    private int productPrice;
    private int quantity;
}
