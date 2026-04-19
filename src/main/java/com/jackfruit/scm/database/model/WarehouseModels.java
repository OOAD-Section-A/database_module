package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class WarehouseModels {

    private WarehouseModels() {
    }

    public record WarehouseZone(String zoneId, String warehouseId, String zoneType) {
    }

    public record Bin(String binId, String zoneId, int binCapacity, String binStatus) {
    }

    public record GoodsReceipt(String goodsReceiptId, String purchaseOrderId, String supplierId,
                               String productId, int orderedQty, int receivedQty,
                               LocalDateTime receivedAt, String conditionStatus) {
    }

    public record StockRecord(String stockId, String productId, String binId, int quantity,
                              LocalDateTime lastUpdated) {
    }

    public record StockMovement(String movementId, String movementType, String fromBin, String toBin,
                                String productId, int movedQty, LocalDateTime movementTimestamp) {
    }

    public record PickTask(String pickTaskId, String orderId, String assignedEmployeeId,
                           String productId, int pickQty, String taskStatus) {
    }

    public record StagingDispatch(String stagingId, String dockDoorId, String orderId,
                                  LocalDateTime dispatchedAt, String shipmentStatus) {
    }

    public record WarehouseReturn(String returnId, String productId, int returnQty,
                                  String conditionStatus, LocalDateTime returnTimestamp) {
    }

    public record CycleCount(String cycleCountId, String productId, String productName, String sku,
                             String employeeId, String employeeName, int expectedQty,
                             int countedQty, LocalDateTime countTimestamp) {
    }

    public record WmsStorageUnitLpn(String lpnId, String unitType, String currentLocationType,
                                    String currentBinId, BigDecimal grossWeightKg, String status,
                                    LocalDateTime createdAt) {
    }

    public record WmsPickWave(String waveId, String warehouseId, String waveType, String status,
                              LocalDateTime createdAt, LocalDateTime releasedAt, int version) {
    }

    public record WmsTaskQueueItem(String taskId, String taskType, String productId,
                                   String sourceLpnId, String targetBinId, String assignedEmployeeId,
                                   int priority, String status, LocalDateTime createdAt, int version) {
    }
}
