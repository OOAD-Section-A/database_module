package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public class StockTransaction {

    private String transactionId;
    private String productId;
    private String batchId;
    private String locationId;
    private Integer quantityChange;
    private String type;
    private String referenceType;
    private String referenceId;
    private LocalDateTime timestamp;

    public StockTransaction() {
    }

    public StockTransaction(String transactionId, String productId, String batchId,
                           String locationId, Integer quantityChange, String type,
                           String referenceType, String referenceId, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.batchId = batchId;
        this.locationId = locationId;
        this.quantityChange = quantityChange;
        this.type = type;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
