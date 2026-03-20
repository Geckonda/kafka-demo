package com.kafkademo.producer_service.service;

import com.kafkademo.producer_service.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducerService {

    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    @Value("${kafka.topics.orders-created}")
    private String ordersCreatedTopic;

    @Value("${kafka.topics.notifications}")
    private String notificationsTopic;

    public void sendOrder(OrderDto order) {
        // Заполняем поля которые генерируются на сервере
        order.setOrderId(UUID.randomUUID().toString());
        order.setCreatedAt(LocalDateTime.now());

        // Отправляем в orders.created
        CompletableFuture<SendResult<String, OrderDto>> future =
                kafkaTemplate.send(ordersCreatedTopic, order.getOrderId(), order);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Order sent successfully → topic: {}, partition: {}, offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send order: {}", ex.getMessage());
            }
        });

        // Отправляем уведомление в notifications
        kafkaTemplate.send(notificationsTopic, order.getOrderId(), order);
        log.info("Notification sent for order: {}", order.getOrderId());
    }
}