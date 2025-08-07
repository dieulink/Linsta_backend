package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.model.*;
import com.linsta.linsta_backend.repository.*;
import com.linsta.linsta_backend.request.OrderDetailRequest;
import com.linsta.linsta_backend.request.OrderRequest;
import com.linsta.linsta_backend.response.OrderDetailResponse;
import com.linsta.linsta_backend.response.OrderResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPayMethodId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        // Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(request.getTotalPrice());
        order.setReceiveAddress(request.getReceiveAddress());
        order.setReceiveName(request.getReceiveName());
        order.setReceivePhone(request.getReceivePhone());
        order.setShipCost(request.getShipCost());
        order.setStatus("Đang chờ đơn vị vận chuyển");
        order.setPaymentMethod(paymentMethod);

        orderRepository.save(order);

        // chi tiết đơn hàng + xóa cart + giảm stock
        for (OrderDetailRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ hàng trong kho.");
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            // tạo chi tiết đơn hàng
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setProductPrice(item.getProductPrice());
            orderDetail.setQuantity(item.getQuantity());

            orderDetailRepository.save(orderDetail);

            // xóa sản phẩm khỏi giỏ hàng
            Optional<CartItem> optionalCartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
            if (optionalCartItem.isPresent() && request.getIsCart()==1) {
                cartRepository.delete(optionalCartItem.get());
            }
        }

        //tạo payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("Chưa thanh toán");

        paymentRepository.save(payment);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> responseList = new ArrayList<>();

        for (Order order : orders) {
            List<OrderDetail> details = orderDetailRepository.findByOrder(order);

            List<OrderDetailResponse> detailResponses = details.stream()
                    .map(detail -> new OrderDetailResponse(
                            detail.getProduct().getId(),
                            detail.getProductPrice(),
                            detail.getQuantity()
                    ))
                    .toList();

            OrderResponse response = new OrderResponse();
            response.setUserId(order.getUser().getId());
            response.setQuantity(order.getQuantity());
            response.setTotalPrice(order.getTotalPrice());
            response.setReceiveAddress(order.getReceiveAddress());
            response.setReceiveName(order.getReceiveName());
            response.setReceivePhone(order.getReceivePhone());
            response.setShipCost(order.getShipCost());
            response.setPayMethodId(order.getPaymentMethod().getId());
            response.setItems(detailResponses);
            response.setStatus(order.getStatus());

            responseList.add(response);
        }

        return responseList;
    }

}
