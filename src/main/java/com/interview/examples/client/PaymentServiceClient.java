package com.interview.examples.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", fallback = PaymentServiceFallback.class)
public interface PaymentServiceClient {
    @PostMapping("/api/payments")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

    @GetMapping("/api/payments/{id}")
    PaymentResponse getPayment(@PathVariable("id") String paymentId);
}

