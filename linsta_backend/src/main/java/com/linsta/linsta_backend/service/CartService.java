package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.model.CartItem;
import com.linsta.linsta_backend.repository.CartRepository;
import com.linsta.linsta_backend.repository.ProductRepository;
import com.linsta.linsta_backend.repository.UserRepository;
import com.linsta.linsta_backend.response.ListCartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public ListCartResponse addToCart(Long productId, Long userId){
        Optional<CartItem> cartItem = cartRepository.findByProductIdAndUserId(productId,userId);

        // sản phẩm chưa thêm vào giỏ hàng
        if (!cartItem.isPresent()){
            CartItem newItem = new CartItem();
            newItem.setProduct(productRepository.findById(productId).get());
            newItem.setUser(userRepository.findById(userId).get());
            newItem.setQuantity(1);
            cartRepository.save(newItem);

            ListCartResponse listCartResponse = new ListCartResponse();
            listCartResponse.setTotal(cartRepository.getTotalProductQuantityByUserId(userId));
            listCartResponse.setCartItems(cartRepository.findAllByUserId(userId));
            return listCartResponse;
        }
        else { //sản phẩm đã thêm giỏ hàng
            cartItem.get().setQuantity(cartItem.get().getQuantity()+1);
            cartRepository.save(cartItem.get());
            ListCartResponse listCartResponse = new ListCartResponse();
            listCartResponse.setTotal(cartRepository.getTotalProductQuantityByUserId(userId));
            listCartResponse.setCartItems(cartRepository.findAllByUserId(userId));
            return listCartResponse;
        }
    }

    public ListCartResponse getListCart(Long userId){
        ListCartResponse listCartResponse = new ListCartResponse();
        listCartResponse.setTotal(cartRepository.getTotalProductQuantityByUserId(userId));
        listCartResponse.setCartItems(cartRepository.findAllByUserId(userId));
        return listCartResponse;
    }

//    public ListCartResponse deleteFromCart(Long productId, Long userId) {
//
//    }
}
