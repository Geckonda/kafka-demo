package com.kafkademo.consumer_notification.consumer;

import com.kafkademo.consumer_notification.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @KafkaListener(
            topics = "notifications",
            groupId = "notification-group"
    )
    public void consumeNotification(
            @Payload OrderDto order,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("=== NOTIFICATION SERVICE ===");
        log.info("Topic: {} | Partition: {} | Offset: {}", topic, partition, offset);
        log.info("Sending notification → customer: {}, product: {}, amount: {}",
                order.getCustomerName(),
                order.getProduct(),
                order.getAmount());

        // Здесь была бы отправка email/SMS
        log.info("Notification sent to customer: {}", order.getCustomerName());
    }
}