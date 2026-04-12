package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.PackagingModels.PackagingJob;
import com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord;
import com.jackfruit.scm.database.model.PackagingModels.RepairRequest;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class PackagingSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public PackagingSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createPackagingJob(PackagingJob job) {
        jdbcOperations.update(
                """
                INSERT INTO packaging_jobs
                (package_id, order_id, quantity, total_amount, discounts, packaging_status, packed_by, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, job.packageId());
                    statement.setString(2, job.orderId());
                    statement.setInt(3, job.quantity());
                    statement.setBigDecimal(4, job.totalAmount());
                    statement.setBigDecimal(5, job.discounts());
                    statement.setString(6, job.packagingStatus());
                    statement.setString(7, job.packedBy());
                    statement.setTimestamp(8, Timestamp.valueOf(job.createdAt()));
                });
    }

    public List<PackagingJob> listPackagingJobs() {
        return jdbcOperations.query(
                "SELECT * FROM packaging_jobs",
                resultSet -> new PackagingJob(
                        resultSet.getString("package_id"),
                        resultSet.getString("order_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("total_amount"),
                        resultSet.getBigDecimal("discounts"),
                        resultSet.getString("packaging_status"),
                        resultSet.getString("packed_by"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createRepairRequest(RepairRequest request) {
        jdbcOperations.update(
                "INSERT INTO repair_requests (request_id, order_id, product_id, defect_details, request_status, requested_at) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, request.requestId());
                    statement.setString(2, request.orderId());
                    statement.setString(3, request.productId());
                    statement.setString(4, request.defectDetails());
                    statement.setString(5, request.requestStatus());
                    statement.setTimestamp(6, Timestamp.valueOf(request.requestedAt()));
                });
    }

    public void createReceiptRecord(ReceiptRecord record) {
        jdbcOperations.update(
                "INSERT INTO receipt_records (receipt_record_id, order_id, package_id, received_amount, receipt_status, recorded_at) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, record.receiptRecordId());
                    statement.setString(2, record.orderId());
                    statement.setString(3, record.packageId());
                    statement.setBigDecimal(4, record.receivedAmount());
                    statement.setString(5, record.receiptStatus());
                    statement.setTimestamp(6, Timestamp.valueOf(record.recordedAt()));
                });
    }
}
