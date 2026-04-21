package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.InventoryItemDao;
import com.jackfruit.scm.database.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public class InventoryItemDaoImpl extends AbstractJdbcDao implements InventoryItemDao {

    @Override
    public void save(InventoryItem inventoryItem) {
        executeUpdate(
                """
                INSERT INTO inventory_items
                (product_id, location_id, total_quantity, reserved_quantity, abc_category, reorder_threshold, safety_stock_level, version)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, inventoryItem.getProductId());
                    statement.setString(2, inventoryItem.getLocationId());
                    statement.setInt(3, inventoryItem.getTotalQuantity());
                    statement.setInt(4, inventoryItem.getReservedQuantity());
                    statement.setString(5, inventoryItem.getAbcCategory().toString());
                    statement.setInt(6, inventoryItem.getReorderThreshold());
                    statement.setInt(7, inventoryItem.getSafetyStockLevel());
                    statement.setInt(8, inventoryItem.getVersion());
                });
    }

    @Override
    public void update(InventoryItem inventoryItem) {
        executeUpdate(
                """
                UPDATE inventory_items
                SET total_quantity = ?, reserved_quantity = ?, abc_category = ?, reorder_threshold = ?, safety_stock_level = ?, version = ?
                WHERE product_id = ? AND location_id = ?
                """,
                statement -> {
                    statement.setInt(1, inventoryItem.getTotalQuantity());
                    statement.setInt(2, inventoryItem.getReservedQuantity());
                    statement.setString(3, inventoryItem.getAbcCategory().toString());
                    statement.setInt(4, inventoryItem.getReorderThreshold());
                    statement.setInt(5, inventoryItem.getSafetyStockLevel());
                    statement.setInt(6, inventoryItem.getVersion());
                    statement.setString(7, inventoryItem.getProductId());
                    statement.setString(8, inventoryItem.getLocationId());
                });
    }

    @Override
    public Optional<InventoryItem> findById(String productId, String locationId) {
        return queryForObject(
                "SELECT * FROM inventory_items WHERE product_id = ? AND location_id = ?",
                resultSet -> new InventoryItem(
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("total_quantity"),
                        resultSet.getInt("reserved_quantity"),
                        resultSet.getString("abc_category").charAt(0),
                        resultSet.getInt("reorder_threshold"),
                        resultSet.getInt("safety_stock_level"),
                        resultSet.getInt("version")),
                statement -> {
                    statement.setString(1, productId);
                    statement.setString(2, locationId);
                });
    }

    @Override
    public List<InventoryItem> findAll() {
        return queryForList(
                "SELECT * FROM inventory_items ORDER BY product_id, location_id",
                resultSet -> new InventoryItem(
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("total_quantity"),
                        resultSet.getInt("reserved_quantity"),
                        resultSet.getString("abc_category").charAt(0),
                        resultSet.getInt("reorder_threshold"),
                        resultSet.getInt("safety_stock_level"),
                        resultSet.getInt("version")));
    }

    @Override
    public List<InventoryItem> findByProductId(String productId) {
        return queryForList(
                "SELECT * FROM inventory_items WHERE product_id = ? ORDER BY location_id",
                resultSet -> new InventoryItem(
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("total_quantity"),
                        resultSet.getInt("reserved_quantity"),
                        resultSet.getString("abc_category").charAt(0),
                        resultSet.getInt("reorder_threshold"),
                        resultSet.getInt("safety_stock_level"),
                        resultSet.getInt("version")),
                statement -> statement.setString(1, productId));
    }

    @Override
    public List<InventoryItem> findByLocationId(String locationId) {
        return queryForList(
                "SELECT * FROM inventory_items WHERE location_id = ? ORDER BY product_id",
                resultSet -> new InventoryItem(
                        resultSet.getString("product_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("total_quantity"),
                        resultSet.getInt("reserved_quantity"),
                        resultSet.getString("abc_category").charAt(0),
                        resultSet.getInt("reorder_threshold"),
                        resultSet.getInt("safety_stock_level"),
                        resultSet.getInt("version")),
                statement -> statement.setString(1, locationId));
    }
}
