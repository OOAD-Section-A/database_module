package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.BarcodeEventDao;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class BarcodeEventDaoImpl extends AbstractJdbcDao implements BarcodeEventDao {

    @Override
    public void save(BarcodeRfidEvent event) {
        executeUpdate(
                """
                INSERT INTO barcode_rfid_events
                (event_id, product_id, event_type, source_device, warehouse_id, event_timestamp, raw_payload)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, event.getEventId());
                    statement.setString(2, event.getProductId());
                    statement.setString(3, event.getEventType());
                    statement.setString(4, event.getSourceDevice());
                    statement.setString(5, event.getWarehouseId());
                    statement.setTimestamp(6, Timestamp.valueOf(event.getEventTimestamp()));
                    statement.setString(7, event.getRawPayload());
                });
    }

    @Override
    public void update(BarcodeRfidEvent event) {
        executeUpdate(
                """
                UPDATE barcode_rfid_events
                SET event_type = ?, source_device = ?, warehouse_id = ?, event_timestamp = ?, raw_payload = ?
                WHERE event_id = ?
                """,
                statement -> {
                    statement.setString(1, event.getEventType());
                    statement.setString(2, event.getSourceDevice());
                    statement.setString(3, event.getWarehouseId());
                    statement.setTimestamp(4, Timestamp.valueOf(event.getEventTimestamp()));
                    statement.setString(5, event.getRawPayload());
                    statement.setString(6, event.getEventId());
                });
    }

    @Override
    public Optional<BarcodeRfidEvent> findById(String eventId) {
        return queryForObject(
                "SELECT * FROM barcode_rfid_events WHERE event_id = ?",
                resultSet -> new BarcodeRfidEvent(
                        resultSet.getString("event_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("event_type"),
                        resultSet.getString("source_device"),
                        resultSet.getString("warehouse_id"),
                        resultSet.getTimestamp("event_timestamp").toLocalDateTime(),
                        resultSet.getString("raw_payload")),
                statement -> statement.setString(1, eventId));
    }

    @Override
    public List<BarcodeRfidEvent> findAll() {
        return queryForList(
                "SELECT * FROM barcode_rfid_events",
                resultSet -> new BarcodeRfidEvent(
                        resultSet.getString("event_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("event_type"),
                        resultSet.getString("source_device"),
                        resultSet.getString("warehouse_id"),
                        resultSet.getTimestamp("event_timestamp").toLocalDateTime(),
                        resultSet.getString("raw_payload")));
    }
}
