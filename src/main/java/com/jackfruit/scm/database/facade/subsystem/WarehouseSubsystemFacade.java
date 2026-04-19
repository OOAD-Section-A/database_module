package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.Warehouse;
import com.jackfruit.scm.database.model.WarehouseModels.Bin;
import com.jackfruit.scm.database.model.WarehouseModels.CycleCount;
import com.jackfruit.scm.database.model.WarehouseModels.GoodsReceipt;
import com.jackfruit.scm.database.model.WarehouseModels.PickTask;
import com.jackfruit.scm.database.model.WarehouseModels.StagingDispatch;
import com.jackfruit.scm.database.model.WarehouseModels.StockRecord;
import com.jackfruit.scm.database.model.WarehouseModels.StockMovement;
import com.jackfruit.scm.database.model.WarehouseModels.WarehouseReturn;
import com.jackfruit.scm.database.model.WarehouseModels.WarehouseZone;
import com.jackfruit.scm.database.model.WarehouseModels.WmsPickWave;
import com.jackfruit.scm.database.model.WarehouseModels.WmsStorageUnitLpn;
import com.jackfruit.scm.database.model.WarehouseModels.WmsTaskQueueItem;
import com.jackfruit.scm.database.service.JdbcOperations;
import com.jackfruit.scm.database.service.WarehouseService;
import java.sql.Timestamp;
import java.util.List;

public class WarehouseSubsystemFacade {

    private final WarehouseService warehouseService;
    private final JdbcOperations jdbcOperations;

    public WarehouseSubsystemFacade(WarehouseService warehouseService, JdbcOperations jdbcOperations) {
        this.warehouseService = warehouseService;
        this.jdbcOperations = jdbcOperations;
    }

    public void registerWarehouse(Warehouse warehouse) {
        warehouseService.createWarehouse(warehouse);
    }

    public List<Warehouse> listWarehouses() {
        return warehouseService.getWarehouses();
    }

    public void createZone(WarehouseZone zone) {
        jdbcOperations.update(
                "INSERT INTO warehouse_zones (zone_id, warehouse_id, zone_type) VALUES (?, ?, ?)",
                statement -> {
                    statement.setString(1, zone.zoneId());
                    statement.setString(2, zone.warehouseId());
                    statement.setString(3, zone.zoneType());
                });
    }

    public List<WarehouseZone> listZones() {
        return jdbcOperations.query(
                "SELECT zone_id, warehouse_id, zone_type FROM warehouse_zones",
                resultSet -> new WarehouseZone(
                        resultSet.getString("zone_id"),
                        resultSet.getString("warehouse_id"),
                        resultSet.getString("zone_type")));
    }

    public void createBin(Bin bin) {
        jdbcOperations.update(
                "INSERT INTO bins (bin_id, zone_id, bin_capacity, bin_status) VALUES (?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, bin.binId());
                    statement.setString(2, bin.zoneId());
                    statement.setInt(3, bin.binCapacity());
                    statement.setString(4, bin.binStatus());
                });
    }

    public void createGoodsReceipt(GoodsReceipt goodsReceipt) {
        jdbcOperations.update(
                """
                INSERT INTO goods_receipts
                (goods_receipt_id, purchase_order_id, supplier_id, product_id, ordered_qty, received_qty, received_at, condition_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, goodsReceipt.goodsReceiptId());
                    statement.setString(2, goodsReceipt.purchaseOrderId());
                    statement.setString(3, goodsReceipt.supplierId());
                    statement.setString(4, goodsReceipt.productId());
                    statement.setInt(5, goodsReceipt.orderedQty());
                    statement.setInt(6, goodsReceipt.receivedQty());
                    statement.setTimestamp(7, Timestamp.valueOf(goodsReceipt.receivedAt()));
                    statement.setString(8, goodsReceipt.conditionStatus());
                });
    }

    public void createStockRecord(StockRecord stockRecord) {
        jdbcOperations.update(
                "INSERT INTO stock_records (stock_id, product_id, bin_id, quantity, last_updated) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, stockRecord.stockId());
                    statement.setString(2, stockRecord.productId());
                    statement.setString(3, stockRecord.binId());
                    statement.setInt(4, stockRecord.quantity());
                    statement.setTimestamp(5, Timestamp.valueOf(stockRecord.lastUpdated()));
                });
    }

    public List<StockRecord> listStockRecords() {
        return jdbcOperations.query(
                "SELECT stock_id, product_id, bin_id, quantity, last_updated FROM stock_records",
                resultSet -> new StockRecord(
                        resultSet.getString("stock_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("bin_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getTimestamp("last_updated").toLocalDateTime()));
    }

    public void createStockMovement(StockMovement stockMovement) {
        jdbcOperations.update(
                """
                INSERT INTO stock_movements
                (movement_id, movement_type, from_bin, to_bin, product_id, moved_qty, movement_ts)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stockMovement.movementId());
                    statement.setString(2, stockMovement.movementType());
                    statement.setString(3, stockMovement.fromBin());
                    statement.setString(4, stockMovement.toBin());
                    statement.setString(5, stockMovement.productId());
                    statement.setInt(6, stockMovement.movedQty());
                    statement.setTimestamp(7, Timestamp.valueOf(stockMovement.movementTimestamp()));
                });
    }

    public void createPickTask(PickTask pickTask) {
        jdbcOperations.update(
                """
                INSERT INTO pick_tasks
                (pick_task_id, order_id, assigned_employee_id, product_id, pick_qty, task_status)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, pickTask.pickTaskId());
                    statement.setString(2, pickTask.orderId());
                    statement.setString(3, pickTask.assignedEmployeeId());
                    statement.setString(4, pickTask.productId());
                    statement.setInt(5, pickTask.pickQty());
                    statement.setString(6, pickTask.taskStatus());
                });
    }

    public void createStagingDispatch(StagingDispatch stagingDispatch) {
        jdbcOperations.update(
                "INSERT INTO staging_dispatch (staging_id, dock_door_id, order_id, dispatched_at, shipment_status) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, stagingDispatch.stagingId());
                    statement.setString(2, stagingDispatch.dockDoorId());
                    statement.setString(3, stagingDispatch.orderId());
                    statement.setTimestamp(4, stagingDispatch.dispatchedAt() == null ? null : Timestamp.valueOf(stagingDispatch.dispatchedAt()));
                    statement.setString(5, stagingDispatch.shipmentStatus());
                });
    }

    public void createWarehouseReturn(WarehouseReturn warehouseReturn) {
        jdbcOperations.update(
                "INSERT INTO warehouse_returns (return_id, product_id, return_qty, condition_status, return_ts) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, warehouseReturn.returnId());
                    statement.setString(2, warehouseReturn.productId());
                    statement.setInt(3, warehouseReturn.returnQty());
                    statement.setString(4, warehouseReturn.conditionStatus());
                    statement.setTimestamp(5, Timestamp.valueOf(warehouseReturn.returnTimestamp()));
                });
    }

    public void createCycleCount(CycleCount cycleCount) {
        jdbcOperations.update(
                """
                INSERT INTO cycle_counts
                (cycle_count_id, product_id, product_name, sku, employee_id, employee_name, expected_qty, counted_qty, count_ts)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, cycleCount.cycleCountId());
                    statement.setString(2, cycleCount.productId());
                    statement.setString(3, cycleCount.productName());
                    statement.setString(4, cycleCount.sku());
                    statement.setString(5, cycleCount.employeeId());
                    statement.setString(6, cycleCount.employeeName());
                    statement.setInt(7, cycleCount.expectedQty());
                    statement.setInt(8, cycleCount.countedQty());
                    statement.setTimestamp(9, Timestamp.valueOf(cycleCount.countTimestamp()));
                });
    }

    public void createStorageUnitLpn(WmsStorageUnitLpn storageUnit) {
        jdbcOperations.update(
                """
                INSERT INTO wms_storage_units_lpn
                (lpn_id, unit_type, current_location_type, current_bin_id, gross_weight_kg, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, storageUnit.lpnId());
                    statement.setString(2, storageUnit.unitType());
                    statement.setString(3, storageUnit.currentLocationType());
                    statement.setString(4, storageUnit.currentBinId());
                    statement.setBigDecimal(5, storageUnit.grossWeightKg());
                    statement.setString(6, storageUnit.status());
                    statement.setTimestamp(7, Timestamp.valueOf(storageUnit.createdAt()));
                });
    }

    public void updateStorageUnitLpn(WmsStorageUnitLpn storageUnit) {
        jdbcOperations.update(
                """
                UPDATE wms_storage_units_lpn
                SET unit_type = ?, current_location_type = ?, current_bin_id = ?, gross_weight_kg = ?, status = ?
                WHERE lpn_id = ?
                """,
                statement -> {
                    statement.setString(1, storageUnit.unitType());
                    statement.setString(2, storageUnit.currentLocationType());
                    statement.setString(3, storageUnit.currentBinId());
                    statement.setBigDecimal(4, storageUnit.grossWeightKg());
                    statement.setString(5, storageUnit.status());
                    statement.setString(6, storageUnit.lpnId());
                });
    }

    public List<WmsStorageUnitLpn> listStorageUnitLpns() {
        return jdbcOperations.query(
                "SELECT lpn_id, unit_type, current_location_type, current_bin_id, gross_weight_kg, status, created_at FROM wms_storage_units_lpn",
                resultSet -> new WmsStorageUnitLpn(
                        resultSet.getString("lpn_id"),
                        resultSet.getString("unit_type"),
                        resultSet.getString("current_location_type"),
                        resultSet.getString("current_bin_id"),
                        resultSet.getBigDecimal("gross_weight_kg"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createPickWave(WmsPickWave pickWave) {
        jdbcOperations.update(
                """
                INSERT INTO wms_pick_waves
                (wave_id, warehouse_id, wave_type, status, created_at, released_at, version)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, pickWave.waveId());
                    statement.setString(2, pickWave.warehouseId());
                    statement.setString(3, pickWave.waveType());
                    statement.setString(4, pickWave.status());
                    statement.setTimestamp(5, Timestamp.valueOf(pickWave.createdAt()));
                    statement.setTimestamp(6, pickWave.releasedAt() == null ? null : Timestamp.valueOf(pickWave.releasedAt()));
                    statement.setInt(7, pickWave.version());
                });
    }

    public void updatePickWave(WmsPickWave pickWave) {
        jdbcOperations.update(
                """
                UPDATE wms_pick_waves
                SET warehouse_id = ?, wave_type = ?, status = ?, released_at = ?, version = version + 1
                WHERE wave_id = ? AND version = ?
                """,
                statement -> {
                    statement.setString(1, pickWave.warehouseId());
                    statement.setString(2, pickWave.waveType());
                    statement.setString(3, pickWave.status());
                    statement.setTimestamp(4, pickWave.releasedAt() == null ? null : Timestamp.valueOf(pickWave.releasedAt()));
                    statement.setString(5, pickWave.waveId());
                    statement.setInt(6, pickWave.version());
                });
    }

    public List<WmsPickWave> listPickWaves() {
        return jdbcOperations.query(
                "SELECT wave_id, warehouse_id, wave_type, status, created_at, released_at, version FROM wms_pick_waves",
                resultSet -> new WmsPickWave(
                        resultSet.getString("wave_id"),
                        resultSet.getString("warehouse_id"),
                        resultSet.getString("wave_type"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getTimestamp("released_at") == null ? null : resultSet.getTimestamp("released_at").toLocalDateTime(),
                        resultSet.getInt("version")));
    }

    public void createTaskQueueItem(WmsTaskQueueItem taskQueueItem) {
        jdbcOperations.update(
                """
                INSERT INTO wms_task_queue
                (task_id, task_type, product_id, source_lpn_id, target_bin_id, assigned_employee_id, priority, status, created_at, version)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, taskQueueItem.taskId());
                    statement.setString(2, taskQueueItem.taskType());
                    statement.setString(3, taskQueueItem.productId());
                    statement.setString(4, taskQueueItem.sourceLpnId());
                    statement.setString(5, taskQueueItem.targetBinId());
                    statement.setString(6, taskQueueItem.assignedEmployeeId());
                    statement.setInt(7, taskQueueItem.priority());
                    statement.setString(8, taskQueueItem.status());
                    statement.setTimestamp(9, Timestamp.valueOf(taskQueueItem.createdAt()));
                    statement.setInt(10, taskQueueItem.version());
                });
    }

    public void updateTaskQueueItem(WmsTaskQueueItem taskQueueItem) {
        jdbcOperations.update(
                """
                UPDATE wms_task_queue
                SET task_type = ?, product_id = ?, source_lpn_id = ?, target_bin_id = ?,
                    assigned_employee_id = ?, priority = ?, status = ?, version = version + 1
                WHERE task_id = ? AND version = ?
                """,
                statement -> {
                    statement.setString(1, taskQueueItem.taskType());
                    statement.setString(2, taskQueueItem.productId());
                    statement.setString(3, taskQueueItem.sourceLpnId());
                    statement.setString(4, taskQueueItem.targetBinId());
                    statement.setString(5, taskQueueItem.assignedEmployeeId());
                    statement.setInt(6, taskQueueItem.priority());
                    statement.setString(7, taskQueueItem.status());
                    statement.setString(8, taskQueueItem.taskId());
                    statement.setInt(9, taskQueueItem.version());
                });
    }

    public List<WmsTaskQueueItem> listTaskQueueItems() {
        return jdbcOperations.query(
                """
                SELECT task_id, task_type, product_id, source_lpn_id, target_bin_id,
                       assigned_employee_id, priority, status, created_at, version
                FROM wms_task_queue
                """,
                resultSet -> new WmsTaskQueueItem(
                        resultSet.getString("task_id"),
                        resultSet.getString("task_type"),
                        resultSet.getString("product_id"),
                        resultSet.getString("source_lpn_id"),
                        resultSet.getString("target_bin_id"),
                        resultSet.getString("assigned_employee_id"),
                        resultSet.getInt("priority"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getInt("version")));
    }
}
