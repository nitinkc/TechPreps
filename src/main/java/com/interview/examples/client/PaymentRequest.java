package com.interview.examples.client;

public class PaymentRequest {
    private Long customerId;
    private Double amount;
    private String orderId;

    public PaymentRequest() {}

    public PaymentRequest(Long customerId, Double amount, String orderId) {
        this.customerId = customerId;
        this.amount = amount;
        this.orderId = orderId;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}

