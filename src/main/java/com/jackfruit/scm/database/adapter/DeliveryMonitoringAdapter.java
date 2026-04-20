package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingEvent;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingRoute;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingWaypoint;
import java.util.List;

public class DeliveryMonitoringAdapter {

    private final SupplyChainDatabaseFacade facade;

    public DeliveryMonitoringAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createTrackingRoute(DeliveryTrackingRoute route) {
        facade.deliveryMonitoring().createTrackingRoute(route);
    }

    public void deleteTrackingRoute(String routePlanId) {
        facade.deliveryMonitoring().deleteTrackingRoute(routePlanId);
    }

    public List<DeliveryTrackingRoute> listTrackingRoutes() {
        return facade.deliveryMonitoring().listTrackingRoutes();
    }

    public void createTrackingWaypoint(DeliveryTrackingWaypoint waypoint) {
        facade.deliveryMonitoring().createTrackingWaypoint(waypoint);
    }

    public void deleteTrackingWaypoint(String waypointId) {
        facade.deliveryMonitoring().deleteTrackingWaypoint(waypointId);
    }

    public void createTrackingEvent(DeliveryTrackingEvent event) {
        facade.deliveryMonitoring().createTrackingEvent(event);
    }

    public void deleteTrackingEvent(String trackingEventId) {
        facade.deliveryMonitoring().deleteTrackingEvent(trackingEventId);
    }
}
