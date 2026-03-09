package com.interview.examples.service;

import com.interview.examples.model.Order;
import com.interview.examples.dto.CreateOrderRequest;
import com.interview.examples.client.PaymentRequest;
import com.interview.examples.client.PaymentResponse;
import com.interview.examples.client.PaymentServiceClient;
import com.interview.examples.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentServiceClient paymentService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(request.getCustomerId(), request.getAmount());
        order = orderRepository.save(order);

        // Make the saved order effectively final for lambda capture
        final Order savedOrder = order;

        CompletableFuture.runAsync(() -> {
            try {
                PaymentRequest paymentRequest = new PaymentRequest(
                    savedOrder.getCustomerId(),
                    savedOrder.getAmount(),
                    savedOrder.getId().toString()
                );

                PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);

                if ("SUCCESS".equals(paymentResponse.getStatus())) {
                    savedOrder.setStatus(com.interview.examples.model.OrderStatus.CONFIRMED);
                    orderRepository.save(savedOrder);
                    kafkaTemplate.send("order-events", "order.confirmed", savedOrder);
                } else {
                    savedOrder.setStatus(com.interview.examples.model.OrderStatus.CANCELLED);
                    orderRepository.save(savedOrder);
                }
            } catch (Exception e) {
                savedOrder.setStatus(com.interview.examples.model.OrderStatus.CANCELLED);
                orderRepository.save(savedOrder);
            }
        });

        return savedOrder;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerIdAndStatus(customerId, com.interview.examples.model.OrderStatus.CONFIRMED);
    }
}
