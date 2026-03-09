package com.interview.examples.controller;

import com.interview.examples.dto.OrderResponse;
import com.interview.examples.dto.CreateOrderRequest;
import com.interview.examples.service.OrderService;
import com.interview.examples.model.Order;
import com.interview.examples.model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@PathVariable Long customerId) {
        List<Order> orders = orderService.getCustomerOrders(customerId);
        List<OrderResponse> responses = orders.stream()
            .map(OrderResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        Order order = orderService.findById(id);
        order.setStatus(status);
        order = orderService.updateOrder(order);

        return ResponseEntity.ok(OrderResponse.from(order));
    }
}

