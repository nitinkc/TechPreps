package com.interview.examples.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CreateOrderRequest {
    @NotNull
    private Long customerId;

    @NotNull
    @Positive
    private Double amount;

    private String productId;
    private Integer quantity;

    public CreateOrderRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}

