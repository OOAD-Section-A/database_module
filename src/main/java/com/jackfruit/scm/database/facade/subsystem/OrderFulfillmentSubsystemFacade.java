package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.OrderFulfillmentModels.FulfillmentOrder;
import com.jackfruit.scm.database.model.OrderFulfillmentModels.PackingDetail;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class OrderFulfillmentSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public OrderFulfillmentSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createFulfillmentOrder(FulfillmentOrder fulfillmentOrder) {
        jdbcOperations.update(
                "INSERT INTO fulfillment_orders (fulfillment_id, order_id, fulfillment_status, assigned_warehouse, priority_level, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, fulfillmentOrder.fulfillmentId());
                    statement.setString(2, fulfillmentOrder.orderId());
                    statement.setString(3, fulfillmentOrder.fulfillmentStatus());
                    statement.setString(4, fulfillmentOrder.assignedWarehouse());
                    statement.setString(5, fulfillmentOrder.priorityLevel());
                    statement.setTimestamp(6, Timestamp.valueOf(fulfillmentOrder.createdAt()));
                });
    }

    public List<FulfillmentOrder> listFulfillmentOrders() {
        return jdbcOperations.query(
                "SELECT * FROM fulfillment_orders",
                resultSet -> new FulfillmentOrder(
                        resultSet.getString("fulfillment_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("fulfillment_status"),
                        resultSet.getString("assigned_warehouse"),
                        resultSet.getString("priority_level"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createPackingDetail(PackingDetail packingDetail) {
        jdbcOperations.update(
                "INSERT INTO packing_details (packing_id, fulfillment_id, package_type, packed_by, packed_at, package_weight) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, packingDetail.packingId());
                    statement.setString(2, packingDetail.fulfillmentId());
                    statement.setString(3, packingDetail.packageType());
                    statement.setString(4, packingDetail.packedBy());
                    statement.setTimestamp(5, Timestamp.valueOf(packingDetail.packedAt()));
                    statement.setBigDecimal(6, packingDetail.packageWeight());
                });
    }
}
