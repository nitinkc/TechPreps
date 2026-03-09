package com.interview.examples.events;

public class InventoryEvent {
    private String productId;
    private Integer quantity;
    private String operation;

    public InventoryEvent() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    @Override
    public String toString() {
        return "InventoryEvent{productId='" + productId + "', quantity=" + quantity + ", operation='" + operation + "'}";
    }
}
