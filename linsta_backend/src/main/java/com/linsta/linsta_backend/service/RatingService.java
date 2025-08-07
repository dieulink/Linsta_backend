package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.model.Product;
import com.linsta.linsta_backend.model.Rating;
import com.linsta.linsta_backend.model.User;
import com.linsta.linsta_backend.repository.*;
import com.linsta.linsta_backend.request.RatingRequest;
import com.linsta.linsta_backend.response.RatingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public Map<String, Object> addRatingIfPurchased(RatingRequest request) {
        Map<String, Object> result = new HashMap<>();

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Kiểm tra xem user đã mua product chưa
        boolean hasPurchased = orderDetailRepository.findAll().stream()
                .anyMatch(od ->
                        od.getProduct().getId().equals(product.getId()) &&
                                od.getOrder().getUser().getId().equals(user.getId())
                );

        if (!hasPurchased) {
            result.put("message", "Người dùng chưa mua hàng");
            result.put("ratings", "");
            return result;
        }

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setProduct(product);
        rating.setScore(request.getScore());
        rating.setComment(request.getComment());

        ratingRepository.save(rating);

        result.put("message", "Đánh giá thành công");
        result.put("ratings", getRatingsByProductId(product.getId()));
        return result;
    }

    private List<RatingResponse> getRatingsByProductId(Long productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);

        return ratings.stream().map(rating -> {
            RatingResponse response = new RatingResponse();
            response.setUserName(rating.getUser().getName());
            response.setScore(rating.getScore());
            response.setComment(rating.getComment());
            response.setCreatedAt(rating.getCreatedAt());
            return response;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> updateRating(Long ratingId, RatingRequest request) {
        Map<String, Object> result = new HashMap<>();

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        if (!rating.getUser().getId().equals(request.getUserId())) {
            result.put("message", "Bạn không có quyền sửa đánh giá này");
            result.put("ratings", "");
            return result;
        }

        rating.setScore(request.getScore());
        rating.setComment(request.getComment());
        ratingRepository.save(rating);

        result.put("message", "Cập nhật đánh giá thành công");
        result.put("ratings", getRatingsByProductId(rating.getProduct().getId()));
        return result;
    }
    public Map<String, Object> deleteRating(Long ratingId) {
        Map<String, Object> result = new HashMap<>();

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));


        Long productId = rating.getProduct().getId();
        ratingRepository.delete(rating);

        result.put("message", "Xóa đánh giá thành công");
        result.put("ratings", getRatingsByProductId(productId));
        return result;
    }

}
