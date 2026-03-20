package com.kafkademo.consumer_inventory.consumer;

import com.kafkademo.consumer_inventory.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryConsumer {

    @KafkaListener(
            topics = {"orders.created", "orders.processed"},
            groupId = "inventory-group"
    )
    public void consumeOrder(
            @Payload OrderDto order,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("=== INVENTORY SERVICE ===");
        log.info("Topic: {} | Partition: {} | Offset: {}", topic, partition, offset);
        log.info("Processing order → id: {}, customer: {}, product: {}, amount: {}",
                order.getOrderId(),
                order.getCustomerName(),
                order.getProduct(),
                order.getAmount());

        // Здесь была бы логика обновления склада
        log.info("Inventory updated for order: {}", order.getOrderId());
    }
}