package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.DemandForecastDao;
import com.jackfruit.scm.database.model.DemandForecast;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class DemandForecastDaoImpl extends AbstractJdbcDao implements DemandForecastDao {

    @Override
    public void save(DemandForecast forecast) {
        executeUpdate(
                """
                INSERT INTO demand_forecasts
                (forecast_id, product_id, forecast_period, predicted_demand, confidence_score, generated_at, source_event_reference)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, forecast.getForecastId());
                    statement.setString(2, forecast.getProductId());
                    statement.setString(3, forecast.getForecastPeriod());
                    statement.setInt(4, forecast.getPredictedDemand());
                    statement.setBigDecimal(5, forecast.getConfidenceScore());
                    statement.setTimestamp(6, Timestamp.valueOf(forecast.getGeneratedAt()));
                    statement.setString(7, forecast.getSourceEventReference());
                });
    }

    @Override
    public void update(DemandForecast forecast) {
        executeUpdate(
                """
                UPDATE demand_forecasts
                SET predicted_demand = ?, confidence_score = ?, generated_at = ?, source_event_reference = ?
                WHERE forecast_id = ?
                """,
                statement -> {
                    statement.setInt(1, forecast.getPredictedDemand());
                    statement.setBigDecimal(2, forecast.getConfidenceScore());
                    statement.setTimestamp(3, Timestamp.valueOf(forecast.getGeneratedAt()));
                    statement.setString(4, forecast.getSourceEventReference());
                    statement.setString(5, forecast.getForecastId());
                });
    }

    @Override
    public Optional<DemandForecast> findById(String forecastId) {
        return queryForObject(
                "SELECT * FROM demand_forecasts WHERE forecast_id = ?",
                resultSet -> new DemandForecast(
                        resultSet.getString("forecast_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("forecast_period"),
                        resultSet.getInt("predicted_demand"),
                        resultSet.getBigDecimal("confidence_score"),
                        resultSet.getTimestamp("generated_at").toLocalDateTime(),
                        resultSet.getString("source_event_reference")),
                statement -> statement.setString(1, forecastId));
    }

    @Override
    public List<DemandForecast> findAll() {
        return queryForList(
                "SELECT * FROM demand_forecasts",
                resultSet -> new DemandForecast(
                        resultSet.getString("forecast_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("forecast_period"),
                        resultSet.getInt("predicted_demand"),
                        resultSet.getBigDecimal("confidence_score"),
                        resultSet.getTimestamp("generated_at").toLocalDateTime(),
                        resultSet.getString("source_event_reference")));
    }
}
