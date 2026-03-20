package com.kafkademo.consumer_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String orderId;
    private String customerName;
    private String product;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
