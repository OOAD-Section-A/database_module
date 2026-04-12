package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.InventoryModels.Product;
import com.jackfruit.scm.database.model.InventoryModels.StockLevel;
import com.jackfruit.scm.database.model.Warehouse;
import java.util.List;

public class InventoryAdapter {

    private final SupplyChainDatabaseFacade facade;

    public InventoryAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void syncWarehouse(Warehouse warehouse) {
        facade.warehouse().registerWarehouse(warehouse);
    }

    public void createProduct(Product product) {
        facade.inventory().createProduct(product);
    }

    public void createStockLevel(StockLevel stockLevel) {
        facade.inventory().createStockLevel(stockLevel);
    }

    public List<Product> listProducts() {
        return facade.inventory().listProducts();
    }
}
