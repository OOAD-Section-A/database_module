package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.model.DemandForecastingModels.ForecastPerformanceMetric;
import com.jackfruit.scm.database.model.DemandForecastingModels.SalesRecord;
import com.jackfruit.scm.database.service.ForecastService;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Date;
import java.util.List;

public class DemandForecastingSubsystemFacade {

    private final ForecastService forecastService;
    private final JdbcOperations jdbcOperations;

    public DemandForecastingSubsystemFacade(ForecastService forecastService, JdbcOperations jdbcOperations) {
        this.forecastService = forecastService;
        this.jdbcOperations = jdbcOperations;
    }

    public void createForecast(DemandForecast forecast) {
        forecastService.createForecast(forecast);
    }

    public List<DemandForecast> listForecasts() {
        return forecastService.getForecasts();
    }

    public void createSalesRecord(SalesRecord salesRecord) {
        jdbcOperations.update(
                "INSERT INTO sales_records (sale_id, product_id, store_id, sale_date, quantity_sold, unit_price, revenue, region) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, salesRecord.saleId());
                    statement.setString(2, salesRecord.productId());
                    statement.setString(3, salesRecord.storeId());
                    statement.setDate(4, Date.valueOf(salesRecord.saleDate()));
                    statement.setInt(5, salesRecord.quantitySold());
                    statement.setBigDecimal(6, salesRecord.unitPrice());
                    statement.setBigDecimal(7, salesRecord.revenue());
                    statement.setString(8, salesRecord.region());
                });
    }

    public List<SalesRecord> listSalesRecords() {
        return jdbcOperations.query(
                "SELECT * FROM sales_records",
                resultSet -> new SalesRecord(
                        resultSet.getString("sale_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("store_id"),
                        resultSet.getDate("sale_date").toLocalDate(),
                        resultSet.getInt("quantity_sold"),
                        resultSet.getBigDecimal("unit_price"),
                        resultSet.getBigDecimal("revenue"),
                        resultSet.getString("region")));
    }

    public void createForecastPerformanceMetric(ForecastPerformanceMetric metric) {
        jdbcOperations.update(
                """
                INSERT INTO forecast_performance_metrics
                (eval_id, forecast_id, forecast_date, predicted_qty, actual_qty, mape, rmse, model_used)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, metric.evalId());
                    statement.setString(2, metric.forecastId());
                    statement.setDate(3, Date.valueOf(metric.forecastDate()));
                    statement.setInt(4, metric.predictedQty());
                    if (metric.actualQty() == null) {
                        statement.setNull(5, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(5, metric.actualQty());
                    }
                    statement.setBigDecimal(6, metric.mape());
                    statement.setBigDecimal(7, metric.rmse());
                    statement.setString(8, metric.modelUsed());
                });
    }
}
