package com.interview.examples.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    @KafkaListener(topics = "payment-events", groupId = "order-service")
    public void handlePaymentEvent(PaymentEvent event) {
        System.out.println("Received payment event: " + event);
    }

    @KafkaListener(topics = "inventory-events", groupId = "order-service")
    public void handleInventoryEvent(InventoryEvent event) {
        System.out.println("Received inventory event: " + event);
    }
}

