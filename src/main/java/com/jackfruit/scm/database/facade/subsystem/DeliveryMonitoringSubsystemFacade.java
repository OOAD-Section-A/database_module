package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingEvent;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingRoute;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class DeliveryMonitoringSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public DeliveryMonitoringSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createTrackingRoute(DeliveryTrackingRoute route) {
        jdbcOperations.update(
                """
                INSERT INTO delivery_tracking_routes
                (route_plan_id, delivery_id, carrier_id, tracking_api_url, planned_departure, planned_arrival, current_eta, route_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, route.routePlanId());
                    statement.setString(2, route.deliveryId());
                    statement.setString(3, route.carrierId());
                    statement.setString(4, route.trackingApiUrl());
                    statement.setTimestamp(5, route.plannedDeparture() == null ? null : Timestamp.valueOf(route.plannedDeparture()));
                    statement.setTimestamp(6, route.plannedArrival() == null ? null : Timestamp.valueOf(route.plannedArrival()));
                    statement.setTimestamp(7, route.currentEta() == null ? null : Timestamp.valueOf(route.currentEta()));
                    statement.setString(8, route.routeStatus());
                });
    }

    public List<DeliveryTrackingRoute> listTrackingRoutes() {
        return jdbcOperations.query(
                "SELECT * FROM delivery_tracking_routes",
                resultSet -> new DeliveryTrackingRoute(
                        resultSet.getString("route_plan_id"),
                        resultSet.getString("delivery_id"),
                        resultSet.getString("carrier_id"),
                        resultSet.getString("tracking_api_url"),
                        resultSet.getTimestamp("planned_departure") == null ? null : resultSet.getTimestamp("planned_departure").toLocalDateTime(),
                        resultSet.getTimestamp("planned_arrival") == null ? null : resultSet.getTimestamp("planned_arrival").toLocalDateTime(),
                        resultSet.getTimestamp("current_eta") == null ? null : resultSet.getTimestamp("current_eta").toLocalDateTime(),
                        resultSet.getString("route_status")));
    }

    public void createTrackingEvent(DeliveryTrackingEvent event) {
        jdbcOperations.update(
                """
                INSERT INTO delivery_tracking_events
                (tracking_event_id, delivery_id, rider_id, vehicle_id, timeline_stage, gps_coordinates, event_timestamp, alert_message, requires_rerouting)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, event.trackingEventId());
                    statement.setString(2, event.deliveryId());
                    statement.setString(3, event.riderId());
                    statement.setString(4, event.vehicleId());
                    statement.setString(5, event.timelineStage());
                    statement.setString(6, event.gpsCoordinates());
                    statement.setTimestamp(7, Timestamp.valueOf(event.eventTimestamp()));
                    statement.setString(8, event.alertMessage());
                    statement.setBoolean(9, event.requiresRerouting());
                });
    }
}
