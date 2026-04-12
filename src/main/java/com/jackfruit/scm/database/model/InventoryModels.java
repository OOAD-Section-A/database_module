package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class InventoryModels {

    private InventoryModels() {
    }

    public record StockLevel(String stockLevelId, String productId, int currentStockQty,
                             int reservedStockQty, int availableStockQty, int reorderThreshold,
                             int reorderQuantity, int safetyStockLevel, LocalDateTime lastUpdated) {
    }

    public record Product(String productId, String productName, String sku, String category,
                          String subCategory, String supplierId, String unitOfMeasure,
                          String storageConditions, Integer shelfLifeDays, LocalDateTime createdAt) {
    }

    public record ProductBatch(String batchId, String productId, String lotId, LocalDate manufacturingDate,
                               String supplierId, String batchStatus, LocalDateTime receivedDate) {
    }

    public record ExpiryTracking(String expiryId, String batchId, LocalDate expiryDate,
                                 int daysRemaining, String expiryStatus, boolean alertFlag) {
    }

    public record StockAdjustment(String adjustmentId, String productId, String batchId,
                                  String adjustmentType, int quantityAdjusted, String reason,
                                  String adjustedBy, LocalDateTime adjustedAt) {
    }

    public record ReorderManagement(String reorderId, String productId, int currentStock,
                                    int reorderThreshold, int reorderQuantity, String supplierId,
                                    String reorderStatus, LocalDateTime lastReorderDate) {
    }

    public record StockReservation(String reservationId, String productId, String orderId,
                                   int reservedQty, String reservationStatus, LocalDateTime reservedAt,
                                   LocalDateTime expiryTime) {
    }

    public record StockFreeze(String freezeId, String productId, String batchId, boolean freezeStatus,
                              String freezeReason, String frozenBy, LocalDateTime frozenAt) {
    }

    public record DeadStock(String deadStockId, String productId, LocalDateTime lastMovementDate,
                            int stagnantDays, int stagnantQuantity, String actionFlag) {
    }

    public record StockValuation(String valuationId, String productId, BigDecimal unitCost,
                                 int totalQuantity, BigDecimal totalValue, BigDecimal reservedValue,
                                 String valuationMethod) {
    }
}
