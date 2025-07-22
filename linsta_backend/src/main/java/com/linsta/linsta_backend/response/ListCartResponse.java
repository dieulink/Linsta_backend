package com.linsta.linsta_backend.response;

import com.linsta.linsta_backend.model.CartItem;
import lombok.Data;

import java.util.List;

@Data
public class ListCartResponse {
    private List<CartItem> cartItems;
    private Long total;
}
