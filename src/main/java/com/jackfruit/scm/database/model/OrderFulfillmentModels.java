package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class OrderFulfillmentModels {

    private OrderFulfillmentModels() {
    }

    public record FulfillmentOrder(String fulfillmentId, String orderId, String fulfillmentStatus,
                                   String assignedWarehouse, String priorityLevel,
                                   LocalDateTime createdAt) {
    }

    public record PackingDetail(String packingId, String fulfillmentId, String packageType,
                                String packedBy, LocalDateTime packedAt, BigDecimal packageWeight) {
    }
}
