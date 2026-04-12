package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.ReportingModels.ExceptionReportRow;
import com.jackfruit.scm.database.model.ReportingModels.InventoryStockReportRow;
import com.jackfruit.scm.database.model.ReportingModels.PriceDiscountReportRow;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.util.List;

public class ReportingSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public ReportingSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public List<InventoryStockReportRow> getInventoryStockReport() {
        return jdbcOperations.query(
                "SELECT * FROM vw_inventory_stock_report",
                resultSet -> new InventoryStockReportRow(
                        resultSet.getString("productID"),
                        resultSet.getString("bin_id"),
                        resultSet.getString("zone_id"),
                        resultSet.getString("warehouseID"),
                        resultSet.getInt("current_stock_level"),
                        resultSet.getTimestamp("last_updated").toLocalDateTime()));
    }

    public List<PriceDiscountReportRow> getPriceDiscountReport() {
        return jdbcOperations.query(
                "SELECT * FROM vw_price_discount_report",
                resultSet -> new PriceDiscountReportRow(
                        resultSet.getString("productID"),
                        resultSet.getBigDecimal("product_price"),
                        resultSet.getString("region_code"),
                        resultSet.getString("channel"),
                        resultSet.getString("currency_code"),
                        resultSet.getString("status")));
    }

    public List<ExceptionReportRow> getExceptionReport() {
        return jdbcOperations.query(
                "SELECT * FROM vw_exception_report",
                resultSet -> new ExceptionReportRow(
                        resultSet.getString("exceptionID"),
                        resultSet.getString("exceptionType"),
                        resultSet.getString("severity_level"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime(),
                        resultSet.getString("requested_by"),
                        resultSet.getString("justification_text")));
    }
}
