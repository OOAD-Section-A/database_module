package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public class InventoryBatch {

    private String batchId;
    private String productId;
    private String locationId;
    private String supplierId;
    private Integer quantity;
    private LocalDateTime arrivalTime;
    private LocalDateTime expiryTime;

    public InventoryBatch() {
    }

    public InventoryBatch(String batchId, String productId, String locationId,
                         String supplierId, Integer quantity, LocalDateTime arrivalTime,
                         LocalDateTime expiryTime) {
        this.batchId = batchId;
        this.productId = productId;
        this.locationId = locationId;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.arrivalTime = arrivalTime;
        this.expiryTime = expiryTime;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
}
