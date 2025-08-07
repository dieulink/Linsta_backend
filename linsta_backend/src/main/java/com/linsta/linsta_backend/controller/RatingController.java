package com.linsta.linsta_backend.controller;

import com.linsta.linsta_backend.model.Rating;
import com.linsta.linsta_backend.repository.OrderDetailRepository;
import com.linsta.linsta_backend.repository.RatingRepository;
import com.linsta.linsta_backend.request.RatingRequest;
import com.linsta.linsta_backend.response.RatingResponse;
import com.linsta.linsta_backend.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

    @Autowired
    private final RatingService ratingService;

    @Autowired
    private RatingRepository ratingRepository;
    private final OrderDetailRepository orderDetailRepository;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> rateProduct(@RequestBody RatingRequest request) {
        return ResponseEntity.ok(ratingService.addRatingIfPurchased(request));
    }

    @GetMapping("/list/{productId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByProductId(@PathVariable Long productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);

        List<RatingResponse> responseList = ratings.stream().map(rating -> {
            RatingResponse response = new RatingResponse();
            response.setUserName(rating.getUser().getName());
            response.setScore(rating.getScore());
            response.setComment(rating.getComment());
            response.setCreatedAt(rating.getCreatedAt());
            response.setId(Math.toIntExact(rating.getId()));
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
    @GetMapping("/list/{productId}/{userId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByProductIdAndUserId(
            @PathVariable Long productId,
            @PathVariable Long userId) {

        List<Rating> ratings;

        if (userId != null) {
            ratings = ratingRepository.findByProductIdAndUserId(productId, userId);
        } else {
            ratings = ratingRepository.findByProductId(productId);
        }

        List<RatingResponse> responseList = ratings.stream().map(rating -> {
            RatingResponse response = new RatingResponse();
            response.setUserName(rating.getUser().getName());
            response.setScore(rating.getScore());
            response.setComment(rating.getComment());
            response.setCreatedAt(rating.getCreatedAt());
            response.setId(Math.toIntExact(rating.getId()));
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }


    @PostMapping("/update/{ratingId}")
    public ResponseEntity<Map<String, Object>> updateRating(
            @PathVariable Long ratingId,
            @RequestBody RatingRequest request
    ) {
        return ResponseEntity.ok(ratingService.updateRating(ratingId, request));
    }

    @DeleteMapping("/delete/{ratingId}")
    public ResponseEntity<Map<String, Object>> deleteRating(
            @PathVariable Long ratingId
    ) {
        return ResponseEntity.ok(ratingService.deleteRating(ratingId));
    }
    @GetMapping("/check_purchased")
    public ResponseEntity<Integer> checkIfUserPurchasedProduct(
            @RequestParam("userId") int userId,
            @RequestParam("productId") int productId
    )
    {
        boolean hasPurchased = orderDetailRepository.findAll().stream()
                .anyMatch(od ->
                        od.getProduct().getId() == productId &&
                                od.getOrder().getUser().getId() == userId
                );

        return ResponseEntity.ok(hasPurchased ? 1 : 0);
    }
}
