package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.StockTransactionDao;
import com.jackfruit.scm.database.model.StockTransaction;
import java.util.List;
import java.util.Optional;

public class StockTransactionDaoImpl extends AbstractJdbcDao implements StockTransactionDao {

    @Override
    public void save(StockTransaction stockTransaction) {
        executeUpdate(
                """
                INSERT INTO stock_transactions
                (transaction_id, product_id, batch_id, location_id, quantity_change, type, reference_type, reference_id, timestamp)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stockTransaction.getTransactionId());
                    statement.setString(2, stockTransaction.getProductId());
                    statement.setString(3, stockTransaction.getBatchId());
                    statement.setString(4, stockTransaction.getLocationId());
                    statement.setInt(5, stockTransaction.getQuantityChange());
                    statement.setString(6, stockTransaction.getType());
                    statement.setString(7, stockTransaction.getReferenceType());
                    statement.setString(8, stockTransaction.getReferenceId());
                    statement.setObject(9, stockTransaction.getTimestamp());
                });
    }

    @Override
    public Optional<StockTransaction> findById(String transactionId) {
        return queryForObject(
                "SELECT * FROM stock_transactions WHERE transaction_id = ?",
                resultSet -> new StockTransaction(
                        resultSet.getString("transaction_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("quantity_change"),
                        resultSet.getString("type"),
                        resultSet.getString("reference_type"),
                        resultSet.getString("reference_id"),
                        resultSet.getObject("timestamp", java.time.LocalDateTime.class)),
                statement -> statement.setString(1, transactionId));
    }

    @Override
    public List<StockTransaction> findAll() {
        return queryForList(
                "SELECT * FROM stock_transactions ORDER BY timestamp DESC",
                resultSet -> new StockTransaction(
                        resultSet.getString("transaction_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("quantity_change"),
                        resultSet.getString("type"),
                        resultSet.getString("reference_type"),
                        resultSet.getString("reference_id"),
                        resultSet.getObject("timestamp", java.time.LocalDateTime.class)));
    }

    @Override
    public List<StockTransaction> findByProductId(String productId) {
        return queryForList(
                "SELECT * FROM stock_transactions WHERE product_id = ? ORDER BY timestamp DESC",
                resultSet -> new StockTransaction(
                        resultSet.getString("transaction_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("quantity_change"),
                        resultSet.getString("type"),
                        resultSet.getString("reference_type"),
                        resultSet.getString("reference_id"),
                        resultSet.getObject("timestamp", java.time.LocalDateTime.class)),
                statement -> statement.setString(1, productId));
    }

    @Override
    public List<StockTransaction> findByProductAndLocation(String productId, String locationId) {
        return queryForList(
                "SELECT * FROM stock_transactions WHERE product_id = ? AND location_id = ? ORDER BY timestamp DESC",
                resultSet -> new StockTransaction(
                        resultSet.getString("transaction_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("quantity_change"),
                        resultSet.getString("type"),
                        resultSet.getString("reference_type"),
                        resultSet.getString("reference_id"),
                        resultSet.getObject("timestamp", java.time.LocalDateTime.class)),
                statement -> {
                    statement.setString(1, productId);
                    statement.setString(2, locationId);
                });
    }

    @Override
    public List<StockTransaction> findByReferenceId(String referenceId) {
        return queryForList(
                "SELECT * FROM stock_transactions WHERE reference_id = ? ORDER BY timestamp DESC",
                resultSet -> new StockTransaction(
                        resultSet.getString("transaction_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getString("location_id"),
                        resultSet.getInt("quantity_change"),
                        resultSet.getString("type"),
                        resultSet.getString("reference_type"),
                        resultSet.getString("reference_id"),
                        resultSet.getObject("timestamp", java.time.LocalDateTime.class)),
                statement -> statement.setString(1, referenceId));
    }
}
