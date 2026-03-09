package com.interview.examples.client;

public class PaymentResponse {
    private String transactionId;
    private String status;
    private String message;

    public PaymentResponse() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

