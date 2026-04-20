package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.OrderFulfillmentModels.FulfillmentOrder;
import com.jackfruit.scm.database.model.OrderFulfillmentModels.PackingDetail;
import java.util.List;

public class OrderFulfillmentAdapter {

    private final SupplyChainDatabaseFacade facade;

    public OrderFulfillmentAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createFulfillmentOrder(FulfillmentOrder fulfillmentOrder) {
        facade.orderFulfillment().createFulfillmentOrder(fulfillmentOrder);
    }

    public void deleteFulfillmentOrder(String fulfillmentId) {
        facade.orderFulfillment().deleteFulfillmentOrder(fulfillmentId);
    }

    public void createPackingDetail(PackingDetail packingDetail) {
        facade.orderFulfillment().createPackingDetail(packingDetail);
    }

    public void deletePackingDetail(String packingId) {
        facade.orderFulfillment().deletePackingDetail(packingId);
    }

    public List<FulfillmentOrder> listFulfillmentOrders() {
        return facade.orderFulfillment().listFulfillmentOrders();
    }
}
