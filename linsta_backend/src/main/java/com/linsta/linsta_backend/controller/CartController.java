package com.linsta.linsta_backend.controller;

import com.linsta.linsta_backend.model.CartItem;
import com.linsta.linsta_backend.model.Category;
import com.linsta.linsta_backend.request.AddCartRequest;
import com.linsta.linsta_backend.response.ListCartResponse;
import com.linsta.linsta_backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/cart")
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ListCartResponse addToCart(@RequestBody AddCartRequest request) {
        return cartService.addToCart(request.getId(), request.getUserId());
    }

    @GetMapping("/list_cart/{userid}")
    public ListCartResponse getListCart(@PathVariable Long userid){
        return cartService.getListCart(userid);
    }

}
