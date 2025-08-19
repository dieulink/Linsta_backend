package com.linsta.linsta_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "total_price",nullable = false)
    private Long totalPrice;

    @Column(name = "receive_address",nullable = false)
    private String receiveAddress;

    @Column(name = "receive_name",nullable = false)
    private String receiveName;

    @Column(name = "receive_phone",nullable = false)
    private String receivePhone;

    @Column(name = "ship_cost",nullable = false)
    private int shipCost;

    @Column(name = "status" ,nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "pay_method_id")
    private PaymentMethod paymentMethod;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "done_at",nullable = true)
    private LocalDateTime doneAt;

}