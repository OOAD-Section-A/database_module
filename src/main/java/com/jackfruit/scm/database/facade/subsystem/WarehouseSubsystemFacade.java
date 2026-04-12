package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.Warehouse;
import com.jackfruit.scm.database.model.WarehouseModels.StockRecord;
import com.jackfruit.scm.database.model.WarehouseModels.WarehouseZone;
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
}
