package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.InventoryModels.Product;
import com.jackfruit.scm.database.model.InventoryModels.ProductBatch;
import com.jackfruit.scm.database.model.InventoryModels.StockLevel;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class InventorySubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public InventorySubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createProduct(Product product) {
        jdbcOperations.update(
                """
                INSERT INTO products
                (product_id, product_name, sku, category, sub_category, supplier_id, unit_of_measure,
                 storage_conditions, shelf_life_days, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, product.productId());
                    statement.setString(2, product.productName());
                    statement.setString(3, product.sku());
                    statement.setString(4, product.category());
                    statement.setString(5, product.subCategory());
                    statement.setString(6, product.supplierId());
                    statement.setString(7, product.unitOfMeasure());
                    statement.setString(8, product.storageConditions());
                    if (product.shelfLifeDays() == null) {
                        statement.setNull(9, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(9, product.shelfLifeDays());
                    }
                    statement.setTimestamp(10, Timestamp.valueOf(product.createdAt()));
                });
    }

    public List<Product> listProducts() {
        return jdbcOperations.query(
                "SELECT * FROM products",
                resultSet -> new Product(
                        resultSet.getString("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("sku"),
                        resultSet.getString("category"),
                        resultSet.getString("sub_category"),
                        resultSet.getString("supplier_id"),
                        resultSet.getString("unit_of_measure"),
                        resultSet.getString("storage_conditions"),
                        (Integer) resultSet.getObject("shelf_life_days"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createProductBatch(ProductBatch batch) {
        jdbcOperations.update(
                "INSERT INTO product_batches (batch_id, product_id, lot_id, manufacturing_date, supplier_id, batch_status, received_date) VALUES (?, ?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, batch.batchId());
                    statement.setString(2, batch.productId());
                    statement.setString(3, batch.lotId());
                    statement.setDate(4, Date.valueOf(batch.manufacturingDate()));
                    statement.setString(5, batch.supplierId());
                    statement.setString(6, batch.batchStatus());
                    statement.setTimestamp(7, Timestamp.valueOf(batch.receivedDate()));
                });
    }

    public void createStockLevel(StockLevel stockLevel) {
        jdbcOperations.update(
                """
                INSERT INTO stock_levels
                (stock_level_id, product_id, current_stock_qty, reserved_stock_qty, available_stock_qty,
                 reorder_threshold, reorder_quantity, safety_stock_level, last_updated)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stockLevel.stockLevelId());
                    statement.setString(2, stockLevel.productId());
                    statement.setInt(3, stockLevel.currentStockQty());
                    statement.setInt(4, stockLevel.reservedStockQty());
                    statement.setInt(5, stockLevel.availableStockQty());
                    statement.setInt(6, stockLevel.reorderThreshold());
                    statement.setInt(7, stockLevel.reorderQuantity());
                    statement.setInt(8, stockLevel.safetyStockLevel());
                    statement.setTimestamp(9, Timestamp.valueOf(stockLevel.lastUpdated()));
                });
    }

    public List<StockLevel> listStockLevels() {
        return jdbcOperations.query(
                "SELECT * FROM stock_levels",
                resultSet -> new StockLevel(
                        resultSet.getString("stock_level_id"),
                        resultSet.getString("product_id"),
                        resultSet.getInt("current_stock_qty"),
                        resultSet.getInt("reserved_stock_qty"),
                        resultSet.getInt("available_stock_qty"),
                        resultSet.getInt("reorder_threshold"),
                        resultSet.getInt("reorder_quantity"),
                        resultSet.getInt("safety_stock_level"),
                        resultSet.getTimestamp("last_updated").toLocalDateTime()));
    }
}
