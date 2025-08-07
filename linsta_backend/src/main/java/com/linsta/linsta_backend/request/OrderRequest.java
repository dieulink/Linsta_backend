package com.linsta.linsta_backend.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private int quantity;
    private Long totalPrice;
    private String receiveAddress;
    private String receiveName;
    private String receivePhone;
    private int shipCost;
    private int isCart;
    private Long payMethodId;
    private List<OrderDetailRequest> items;
}
