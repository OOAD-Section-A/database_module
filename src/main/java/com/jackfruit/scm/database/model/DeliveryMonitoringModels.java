package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public final class DeliveryMonitoringModels {

    private DeliveryMonitoringModels() {
    }

    public record DeliveryTrackingRoute(String routePlanId, String deliveryId, String carrierId,
                                        String trackingApiUrl, LocalDateTime plannedDeparture,
                                        LocalDateTime plannedArrival, LocalDateTime currentEta,
                                        String routeStatus) {
    }

    public record DeliveryTrackingWaypoint(String waypointId, String routePlanId,
                                           int waypointSequence, String waypointLocation) {
    }

    public record DeliveryTrackingEvent(String trackingEventId, String deliveryId, String riderId,
                                        String vehicleId, String timelineStage, String gpsCoordinates,
                                        LocalDateTime eventTimestamp, String alertMessage,
                                        boolean requiresRerouting) {
    }
}
