package com.interview.examples.events;

public class PaymentEvent {
    private String orderId;
    private String status;
    private String transactionId;

    public PaymentEvent() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    @Override
    public String toString() {
        return "PaymentEvent{orderId='" + orderId + "', status='" + status + "', transactionId='" + transactionId + "'}";
    }
}

