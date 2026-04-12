package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public class BarcodeRfidEvent {

    private String eventId;
    private String productId;
    private String eventType;
    private String sourceDevice;
    private String warehouseId;
    private LocalDateTime eventTimestamp;
    private String rawPayload;

    public BarcodeRfidEvent() {
    }

    public BarcodeRfidEvent(String eventId, String productId, String eventType, String sourceDevice,
                            String warehouseId, LocalDateTime eventTimestamp, String rawPayload) {
        this.eventId = eventId;
        this.productId = productId;
        this.eventType = eventType;
        this.sourceDevice = sourceDevice;
        this.warehouseId = warehouseId;
        this.eventTimestamp = eventTimestamp;
        this.rawPayload = rawPayload;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceDevice(String sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }
}
