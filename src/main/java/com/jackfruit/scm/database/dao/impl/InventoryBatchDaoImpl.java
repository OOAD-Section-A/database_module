package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.InventoryBatchDao;
import com.jackfruit.scm.database.model.InventoryBatch;
import java.util.List;
import java.util.Optional;

public class InventoryBatchDaoImpl extends AbstractJdbcDao implements InventoryBatchDao {

    @Override
    public void save(InventoryBatch inventoryBatch) {
        executeUpdate(
                """
                INSERT INTO inventory_batches
                (batch_id, product_id, location_id, supplier_id, quantity, arrival_time, expiry_time)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, inventoryBatch.getBatchId());
                    statement.setString(2, inventoryBatch.getProductId());
                    statement.setString(3, inventoryBatch.getLocationId());
                    statement.setString(4, inventoryBatch.getSupplierId());
                    statement.setInt(5, inventoryBatch.getQuantity());
                    statement.setObject(6, inventoryBatch.getArrivalTime());
                    statement.setObject(7, inventoryBatch.getExpiryTime());
                });
    }

    @Override
    public void update(InventoryBatch inventoryBatch) {
        executeUpdate(
                """
                UPDATE inventory_batches
                SET product_id = ?, location_id = ?, supplier_id = ?, quantity = ?, arrival_time = ?, expiry_time = ?
                WHERE batch_id = ?
                """,
                statement -> {
                    statement.setString(1, inventoryBatch.getProductId());
                    statement.setString(2, inventoryBatch.getLocationId());
                    statement.setString(3, inventoryBatch.getSupplierId());
                    statement.setInt(4, inventoryBatch.getQuantity());
                    statement.setObject(5, inventoryBatch.getArrivalTime());
                    statement.setObject(6, inventoryBatch.getExpiryTime());
                    statement.setString(7, inventoryBatch.getBatchId());
                });
    }

    @Override
    public Optional<InventoryBatch> findById(String batchId) {
        return queryForObject(
                "SELECT * FROM inventory_batches WHERE batch_id = ?",
                resultSet -> new InventoryBatch(
                        resultSet.getString("batch_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getString("supplier_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getObject("arrival_time", java.time.LocalDateTime.class),
                        resultSet.getObject("expiry_time", java.time.LocalDateTime.class)),
                statement -> statement.setString(1, batchId));
    }

    @Override
    public List<InventoryBatch> findAll() {
        return queryForList(
                "SELECT * FROM inventory_batches ORDER BY batch_id",
                resultSet -> new InventoryBatch(
                        resultSet.getString("batch_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getString("supplier_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getObject("arrival_time", java.time.LocalDateTime.class),
                        resultSet.getObject("expiry_time", java.time.LocalDateTime.class)));
    }

    @Override
    public List<InventoryBatch> findByProductId(String productId) {
        return queryForList(
                "SELECT * FROM inventory_batches WHERE product_id = ? ORDER BY batch_id",
                resultSet -> new InventoryBatch(
                        resultSet.getString("batch_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getString("supplier_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getObject("arrival_time", java.time.LocalDateTime.class),
                        resultSet.getObject("expiry_time", java.time.LocalDateTime.class)),
                statement -> statement.setString(1, productId));
    }

    @Override
    public List<InventoryBatch> findByProductAndLocation(String productId, String locationId) {
        return queryForList(
                "SELECT * FROM inventory_batches WHERE product_id = ? AND location_id = ? ORDER BY batch_id",
                resultSet -> new InventoryBatch(
                        resultSet.getString("batch_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getString("supplier_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getObject("arrival_time", java.time.LocalDateTime.class),
                        resultSet.getObject("expiry_time", java.time.LocalDateTime.class)),
                statement -> {
                    statement.setString(1, productId);
                    statement.setString(2, locationId);
                });
    }
}
