package com.linsta.linsta_backend.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponse {
    private int id;
    private String userName;
    private int score;
    private String comment;
    private LocalDateTime createdAt;
}
