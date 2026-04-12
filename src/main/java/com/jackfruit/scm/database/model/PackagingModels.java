package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PackagingModels {

    private PackagingModels() {
    }

    public record PackagingJob(String packageId, String orderId, int quantity, BigDecimal totalAmount,
                               BigDecimal discounts, String packagingStatus, String packedBy,
                               LocalDateTime createdAt) {
    }

    public record RepairRequest(String requestId, String orderId, String productId, String defectDetails,
                                String requestStatus, LocalDateTime requestedAt) {
    }

    public record ReceiptRecord(String receiptRecordId, String orderId, String packageId,
                                BigDecimal receivedAmount, String receiptStatus,
                                LocalDateTime recordedAt) {
    }
}
