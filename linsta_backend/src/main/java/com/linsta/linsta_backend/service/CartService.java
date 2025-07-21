package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.model.CartItem;
import com.linsta.linsta_backend.repository.CartRepository;
import com.linsta.linsta_backend.repository.ProductRepository;
import com.linsta.linsta_backend.repository.UserRepository;
import com.linsta.linsta_backend.response.listCartResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;
import java.util.Optional;

public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public listCartResponse addToCart(Long productId, Long userId){
        Optional<CartItem> cartItem = cartRepository.findCartByUserandPrduct(productId,userId);
        if (!cartItem.isPresent()){
            CartItem newItem = new CartItem();
            newItem.setProduct(productRepository.findById(productId).get());
            newItem.setUser(userRepository.findById(userId).get());
            newItem.setQuantity(1);

            cartRepository.save(newItem);

        }
    }
}
