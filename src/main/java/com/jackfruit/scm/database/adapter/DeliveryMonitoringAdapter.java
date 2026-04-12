package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingEvent;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingRoute;

public class DeliveryMonitoringAdapter {

    private final SupplyChainDatabaseFacade facade;

    public DeliveryMonitoringAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createTrackingRoute(DeliveryTrackingRoute route) {
        facade.deliveryMonitoring().createTrackingRoute(route);
    }

    public void createTrackingEvent(DeliveryTrackingEvent event) {
        facade.deliveryMonitoring().createTrackingEvent(event);
    }
}
