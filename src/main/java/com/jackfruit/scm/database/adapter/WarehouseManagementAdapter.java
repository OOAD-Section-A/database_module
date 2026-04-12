package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.Warehouse;
import com.jackfruit.scm.database.model.WarehouseModels.StockRecord;
import com.jackfruit.scm.database.model.WarehouseModels.WarehouseZone;

public class WarehouseManagementAdapter {

    private final SupplyChainDatabaseFacade facade;

    public WarehouseManagementAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void registerWarehouse(Warehouse warehouse) {
        facade.warehouse().registerWarehouse(warehouse);
    }

    public void createZone(WarehouseZone zone) {
        facade.warehouse().createZone(zone);
    }

    public void createStockRecord(StockRecord stockRecord) {
        facade.warehouse().createStockRecord(stockRecord);
    }
}
