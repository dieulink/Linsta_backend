package com.linsta.linsta_backend.request;

import lombok.Data;

@Data
public class RatingRequest {
    private Long userId;
    private Long productId;
    private int score;
    private String comment;
}
