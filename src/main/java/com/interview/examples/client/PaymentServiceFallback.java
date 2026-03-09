package com.interview.examples.client;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceFallback implements PaymentServiceClient {
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setStatus("FAILED");
        response.setMessage("Payment service unavailable");
        return response;
    }

    @Override
    public PaymentResponse getPayment(String paymentId) {
        PaymentResponse response = new PaymentResponse();
        response.setStatus("UNKNOWN");
        response.setMessage("Payment service unavailable");
        return response;
    }
}

