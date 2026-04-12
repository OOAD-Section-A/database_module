package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.ReturnsModels.ProductReturn;
import com.jackfruit.scm.database.model.ReturnsModels.ReturnGrowthStatistic;

public class ReturnsAdapter {

    private final SupplyChainDatabaseFacade facade;

    public ReturnsAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createProductReturn(ProductReturn productReturn) {
        facade.returns().createProductReturn(productReturn);
    }

    public void createReturnGrowthStatistic(ReturnGrowthStatistic statistic) {
        facade.returns().createReturnGrowthStatistic(statistic);
    }
}
