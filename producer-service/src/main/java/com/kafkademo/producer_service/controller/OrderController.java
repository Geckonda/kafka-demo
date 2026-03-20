package com.kafkademo.producer_service.controller;


import com.kafkademo.producer_service.dto.OrderDto;
import com.kafkademo.producer_service.service.OrderProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducerService producerService;

    @PostMapping
    public ResponseEntity<String> createOrder(@Valid @RequestBody OrderDto order) {
        log.info("Received order request for customer: {}", order.getCustomerName());
        producerService.sendOrder(order);
        return ResponseEntity.ok("Order accepted and sent to Kafka!");
    }
}